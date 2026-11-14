package controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;

/**
 * Request para aplicação de rendimento nas contas poupança.
 */
public class RendimentoRequest {

    @Positive(message = "A taxa de rendimento deve ser positiva.")
    @DecimalMax(value = "1.0", message = "A taxa deve ser informada em decimal (ex: 0.015 para 1.5%). Máximo: 1.0 (100%).")
    private double taxa;

    public double getTaxa() { return taxa; }
    public void setTaxa(double taxa) { this.taxa = taxa; }
}
