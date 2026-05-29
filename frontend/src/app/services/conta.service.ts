import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContaResponse, CriarContaRequest, OperacaoRequest, TransferenciaRequest, RendimentoRequest, ImpostoResponse } from '../models/conta.model';

@Injectable({ providedIn: 'root' })
export class ContaService {
  private api = '/api/contas';

  constructor(private http: HttpClient) {}

  listar(): Observable<ContaResponse[]> {
    return this.http.get<ContaResponse[]>(this.api);
  }

  buscar(numero: number): Observable<ContaResponse> {
    return this.http.get<ContaResponse>(`${this.api}/${numero}`);
  }

  criar(req: CriarContaRequest): Observable<ContaResponse> {
    return this.http.post<ContaResponse>(this.api, req);
  }

  depositar(numero: number, req: OperacaoRequest): Observable<ContaResponse> {
    return this.http.post<ContaResponse>(`${this.api}/${numero}/deposito`, req);
  }

  sacar(numero: number, req: OperacaoRequest): Observable<ContaResponse> {
    return this.http.post<ContaResponse>(`${this.api}/${numero}/saque`, req);
  }

  transferir(req: TransferenciaRequest): Observable<ContaResponse[]> {
    return this.http.post<ContaResponse[]>(`${this.api}/transferencia`, req);
  }

  aplicarRendimento(req: RendimentoRequest): Observable<string> {
    return this.http.post(`${this.api}/rendimento`, req, { responseType: 'text' });
  }

  calcularImpostos(): Observable<ImpostoResponse> {
    return this.http.get<ImpostoResponse>(`${this.api}/impostos`);
  }

  deletar(numero: number): Observable<string> {
    return this.http.delete(`${this.api}/${numero}`, { responseType: 'text' });
  }
}
