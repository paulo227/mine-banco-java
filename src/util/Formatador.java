package util;

import java.text.NumberFormat;
import java.util.Locale;

public class Formatador {

    // Formatar dinheiro em Real (R$)
    public static String formatarDinheiro(double valor) {

        Locale brasil = new Locale("pt", "BR");

        NumberFormat formato = NumberFormat.getCurrencyInstance(brasil);

        return formato.format(valor);
    }
}
