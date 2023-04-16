import java.io.*;

public class Main{
    public static void main(String[] args) throws IOException {
//        Functions.convertCSVtoJSON("C:\\Users\\carlo\\OneDrive - ISCTE-IUL\\Escola\\ISCTE 3ºANO\\2ºSemestre\\ES\\Projeto\\ES-2023-2Sem-Terca-Feira-LEI-GrupoC\\horario.json", "C:\\Users\\carlo\\OneDrive - ISCTE-IUL\\Escola\\ISCTE 3ºANO\\2ºSemestre\\ES\\Projeto\\ES-2023-2Sem-Terca-Feira-LEI-GrupoC\\horario.csv");
//        Functions.convertJSONtoCSV("horario.json");
        Functions.convertJSONtoCSV("horario.json", "horario.csv");
    }
}
