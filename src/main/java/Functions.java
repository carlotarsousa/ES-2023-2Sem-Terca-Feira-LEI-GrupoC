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

    private Functions() {
        throw new IllegalStateException("Aux Functions");
    }

    public static void csvToJSON() throws FileNotFoundException {
        String inputFile = "src/horario_exemplo.csv";
        String outputFile = "horarios.json";
        Logger logger = Logger.getLogger(Main.class.getName());
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            JSONArray jsonArray = new JSONArray();
            boolean firstLine = true;
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
                jsonArray.put(horario);
            }
            try (FileWriter fw = new FileWriter(outputFile)) {
                fw.write(jsonArray.toString(4));
                fw.flush();
            }

            logger.log(Level.INFO, "Arquivo JSON criado com sucesso");
        } catch (IOException | JSONException | NumberFormatException e) {
            throw new FileNotFoundException("Não foi possível abrir ler o ficheiro");
        }
    }

    public static void jsonTocsv() {
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

    public static void convertJsonToCsv(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String fileInString = "";
            String line;
            while ((line = reader.readLine()) != null) {
                fileInString += line;
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(fileInString);

            FileWriter writer = new FileWriter("horario.csv");
            writer.append("Curso;Unidade Curricular;Turno;Turma;Inscritos no turno;Dia da semana;Hora início da aula;Hora fim da aula;Data da aula;Sala atribuída à aula;Lotação da sala\n");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject turno = jsonObject.getJSONObject("Unidade Curricular").getJSONObject("Turno");
                String unidadeCurricular = jsonObject.getJSONObject("Unidade Curricular").getString("Designação");
                String curso = jsonObject.getJSONObject("Unidade Curricular").getJSONArray("Curso").getString(0);
                String turnoId = turno.getString("Id Turno");
                String turma = turno.getJSONArray("Turma").getString(0);
                int inscritos = turno.getInt("Inscritos no turno");
                String horaInicio = turno.getJSONObject("Data da aula").getString("Hora início da aula");
                String horaFim = turno.getJSONObject("Data da aula").getString("Hora fim da aula");
                String data = turno.getJSONObject("Data da aula").getString("Data");
                String diaSemana = turno.getJSONObject("Data da aula").getString("Dia da semana");
                int lotacaoSala = turno.getJSONObject("Data da aula").getJSONObject("Sala atribuída à aula").getInt("Lotação da sala");
                String sala = turno.getJSONObject("Data da aula").getJSONObject("Sala atribuída à aula").getString("Sala");
                writer.append(String.format("%s;%s;%s;%s;%d;%s;%s;%s;%s;%s;%d%n", curso, unidadeCurricular, turnoId, turma, inscritos, diaSemana, horaInicio, horaFim, data, sala, lotacaoSala));
            }
            writer.flush();
            writer.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
