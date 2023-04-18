package functions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test {

    private static final String INPUT_FILE_PATH = "src/horario_exemplo.csv";
    private static final String OUTPUT_FILE_PATH = "test.json";
    private static final String UC = "Unidade Curricular";
    private static final String DATA = "Data da aula";

    @org.junit.jupiter.api.Test
    public void testConvertCSVtoJSON() throws IOException {
        File testFile = new File(INPUT_FILE_PATH);
        try(FileWriter fw = new FileWriter(testFile)){
            fw.write("Curso;Unidade Curricular;Turno;Turma;Inscritos no turno;Dia da semana;Hora início da aula;Hora fim da aula;Data da aula;Sala atribuída à aula;Lotação da sala\n");
            fw.write("ME;Teoria dos Jogos e dos Contratos;01789TP01;MEA1;30;Sex;13:00:00;14:30:00;02/12/2022;AA2.25;34\n");
            fw.write("LETI, LEI, LEI-PL, LIGE, LIGE-PL;Fundamentos de Arquitectura de Computadores;L0705TP23;ET-A9, ET-A8, ET-A7, ET-A12, ET-A11, ET-A10;44;Sex;13:00:00;14:30:00;09/12/2022;C5.06;70\n");

            Functions.convertCSVtoJSON(INPUT_FILE_PATH, OUTPUT_FILE_PATH);

            File outputFile = new File(OUTPUT_FILE_PATH);
            try(BufferedReader br = new BufferedReader(new FileReader(outputFile))){
                String line;
                StringBuilder jsonBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject(UC);
                Assertions.assertEquals("Teoria dos Jogos e dos Contratos", jsonObject.getString("Designação"));
                JSONArray cursoArr = jsonObject.getJSONArray("Curso");
                StringBuilder cursoStr = new StringBuilder();
                for (int j = 0; j < cursoArr.length(); j++) {
                    cursoStr.append(cursoArr.getString(j)).append(", ");
                }
                cursoStr = new StringBuilder(cursoStr.substring(0, cursoStr.length() - 2));
                Assertions.assertEquals("ME", cursoStr.toString());
                JSONObject turno = jsonObject.getJSONObject("Turno");
                Assertions.assertEquals("01789TP01", turno.getString("Id Turno"));
                JSONArray turmaArr = turno.getJSONArray("Turma");
                StringBuilder turmaStr = new StringBuilder();
                for (int k = 0; k < turmaArr.length(); k++) {
                    turmaStr.append(turmaArr.getString(k)).append(", ");
                }
                turmaStr = new StringBuilder(turmaStr.substring(0, turmaStr.length() - 2));
                Assertions.assertEquals("MEA1", turmaStr.toString());
                Assertions.assertEquals("30", Integer.toString(turno.getInt("Inscritos no turno")));
                Assertions.assertEquals("Sex", turno.getJSONObject(DATA).getString("Dia da semana"));
                Assertions.assertEquals("13:00:00", turno.getJSONObject(DATA).getString("Hora início da aula"));
                Assertions.assertEquals("14:30:00", turno.getJSONObject(DATA).getString("Hora fim da aula"));
                Assertions.assertEquals("02/12/2022", turno.getJSONObject(DATA).getString("Data"));
                Assertions.assertEquals("AA2.25", turno.getJSONObject(DATA).getJSONObject("Sala atribuída à aula").getString("Sala"));
                Assertions.assertEquals("34", turno.getJSONObject(DATA).getJSONObject("Sala atribuída à aula").getString("Lotação da sala"));

                testFile.delete();
                outputFile.delete();
            }
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // Create a test JSON file
        String jsonContent = "[{\"UC\":{\"Designação\":\"UC1\",\"Curso\":[\"Curso1\",\"Curso2\"],\"Turno\":{\"Id Turno\":\"T1\",\"Inscritos no turno\":30,\"Turma\":[\"Turma1\",\"Turma2\"],\"DATA\":{\"Hora início da aula\":\"09:00\",\"Hora fim da aula\":\"12:00\",\"Data\":\"2023-04-18\",\"Dia da semana\":\"Quarta-feira\",\"SALA\":{\"Lotação da sala\":\"30\",\"Sala\":\"Sala1\"}}}}}]";
        Files.write(Paths.get(OUTPUT_FILE_PATH), jsonContent.getBytes());
    }

    @org.junit.jupiter.api.Test
    void testConvertJSONtoCSV() throws IOException {
        // Call the function to be tested
        Functions.convertJSONtoCSV(OUTPUT_FILE_PATH, INPUT_FILE_PATH);

        // Assert that the CSV file has been created
        assertTrue(Files.exists(Paths.get(INPUT_FILE_PATH)));

        // Clean up
        Files.deleteIfExists(Paths.get(OUTPUT_FILE_PATH));
        Files.deleteIfExists(Paths.get(INPUT_FILE_PATH));
    }
}
