import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.CDL;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Functions {

    public static void csvToJSON() throws FileNotFoundException {
        String inputFile = "src/horario_exemplo.csv";
        String outputFile = "horarios.json";
        Logger logger = Logger.getLogger(Main.class.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean firstLine = true;
            try (FileWriter fw = new FileWriter(outputFile)) {
                while ((line = br.readLine()) != null) {
                    logger.log(Level.INFO, line);
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] columns = line.split(";", -1);
                    JSONObject sala = new JSONObject();
                    sala.put("Sala", columns[9]);
                    sala.put("Lotação da sala", columns[10]);
                    JSONObject data = new JSONObject();
                    data.put("Data", columns[8]);
                    data.put("Dia da semana", columns[5]);
                    data.put("Hora início da aula", columns[6]);
                    data.put("Hora fim da aula", columns[7]);
                    data.put("Sala atribuída à aula", sala);
                    JSONObject turno = new JSONObject();
                    turno.put("Id Turno", columns[2]);
                    turno.put("Turma", columns[3].split(",\\s*"));
                    turno.put("Inscritos no turno", Integer.parseInt(columns[4]));
                    turno.put("Data da aula", data);
                    JSONObject unidadeCurricular = new JSONObject();
                    unidadeCurricular.put("Designação", columns[1]);
                    unidadeCurricular.put("Curso", columns[0].split(",\\s*"));
                    unidadeCurricular.put("Turno", turno);
                    JSONObject horario = new JSONObject();
                    horario.put("Unidade Curricular", unidadeCurricular);
                    fw.write(horario.toString(4));
                    fw.flush();
                }
            }
            logger.log(Level.INFO, "Arquivo JSON criado com sucesso");
        } catch (IOException | JSONException | NumberFormatException e) {
            throw new FileNotFoundException("Não foi possível abrir ler o ficheiro");
        }
    }

    public static void JSONTocsv() {
        String jsonString;
        JSONObject jsonObject;
        try {
            jsonString = new String(Files.readAllBytes(Paths.get("horarios.json")));
            jsonObject = new JSONObject(jsonString);
            JSONArray docs = jsonObject.getJSONArray("horario");
            File file = new File("output.csv");
            String csvString = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csvString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
