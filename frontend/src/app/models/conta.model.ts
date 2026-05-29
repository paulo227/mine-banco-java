export interface ContaResponse {
  numero: number;
  tipo: string;
  saldo: number;
  limite: number;
  clienteNome: string;
  clienteCpf: string;
  imposto: number;
}

export interface CriarContaRequest {
  nome: string;
  cpf: string;
  numero: number;
  tipo: number;
  limite: number;
}

export interface OperacaoRequest {
  numero: number;
  valor: number;
}

export interface TransferenciaRequest {
  origem: number;
  destino: number;
  valor: number;
}

export interface RendimentoRequest {
  taxa: number;
}

export interface ImpostoResponse {
  totalImpostos: number;
}
