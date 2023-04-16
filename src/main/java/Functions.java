import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Functions {

    private Functions() {
        throw new IllegalStateException("Aux Functions");
    }

    public static void csvToJSON(String loadFilePath, String saveFilePath) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Main.class.getName());
        try (BufferedReader br = loadFile(loadFilePath)) {
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

            try (BufferedWriter bw = saveFile(saveFilePath)) {
                if(bw != null) {
                    bw.write(jsonArray.toString(4));
                }
            }

            logger.log(Level.INFO, "Arquivo JSON criado com sucesso");
        } catch (IOException e) {
            throw new FileNotFoundException("Erro ao ler o ficheiro CSV: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException("Erro ao converter de CSV para JSON: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao converter de CSV para JSON: formato de número inválido: " + e.getMessage(), e);
        }
    }

    public static void jsonToCsv(String jsonFile, String csvFile) throws IOException, JSONException {
        JSONArray jsonArray;
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            jsonArray = new JSONArray(reader.readLine());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            JSONObject headers = jsonArray.getJSONObject(0);
            for (String key : headers.keySet()) {
                writer.write(key);
                if (key != headers.keySet().toArray()[headers.keySet().size() - 1]) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                for (String key : headers.keySet()) {
                    writer.write(obj.get(key).toString());
                    if (key != headers.keySet().toArray()[headers.keySet().size() - 1]) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        }
    }

    public static void convertJsonToCsv(String fileName) {
        try {
            String fileInString;
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                fileInString = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    fileInString += line;
                }
            }

            JSONArray jsonArray = new JSONArray(fileInString);

            try (FileWriter writer = new FileWriter("horario.csv")) {
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
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader loadFile(String filePath) {
        try {
            URL url = new URL(filePath);
            return new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedWriter saveFile(String filePath) {
        try {
            URL url = new URL(filePath);
            return new BufferedWriter(new OutputStreamWriter(url.openConnection().getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
