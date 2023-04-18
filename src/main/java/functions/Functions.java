package functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Functions {

    private static final String SALA = "Sala atribuída à aula";
    private static final String UC = "Unidade Curricular";
    private static final String DATA = "Data da aula";

    private static final Logger logger = Logger.getLogger(Functions.class.getName());

    private Functions() {
        throw new IllegalStateException("Aux functions.Main.Functions");
    }

    /**

     Converts a CSV file containing a schedule of classes to a JSON file.

     Each row in the CSV file represents a class and must have the following columns in this order:
     Curso, Unidade Curricular, Id Turno, Turma, Inscritos no turno, Dia da semana, Hora início da aula, Hora fim da aula,
     Data da aula, Sala atribuída à aula, Lotação da sala.

     The resulting JSON file will have an array of objects, each representing a class.

     @param loadFilePath the path of the CSV file to be converted.
     @param saveFilePath the path of the JSON file to be saved.
     @throws FileNotFoundException if the CSV file specified by loadFilePath cannot be found.
     */
    public static void convertCSVtoJSON(String loadFilePath, String saveFilePath) throws FileNotFoundException {
        try (BufferedReader br = loadFile(loadFilePath)) {
            if(br != null){
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
                    data.put(SALA, sala);
                    JSONObject turno = new JSONObject();
                    turno.put("Id Turno", columns[2]);
                    turno.put("Turma", columns[3].split(",\\s*"));
                    turno.put("Inscritos no turno", Integer.parseInt(columns[4]));
                    turno.put(DATA, data);
                    JSONObject unidadeCurricular = new JSONObject();
                    unidadeCurricular.put("Designação", columns[1]);
                    unidadeCurricular.put("Curso", columns[0].split(",\\s*"));
                    unidadeCurricular.put("Turno", turno);
                    JSONObject horario = new JSONObject();
                    horario.put(UC, unidadeCurricular);
                    jsonArray.put(horario);
                }

                try (BufferedWriter bw = saveFile(saveFilePath)) {
                    if(bw != null) {
                        bw.write(jsonArray.toString(4));
                    }
                }
                logger.log(Level.INFO, "Arquivo JSON criado com sucesso");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Erro ao ler o ficheiro CSV: " + e.getMessage());
        } catch (JSONException e) {
            logger.log(Level.INFO, "Erro ao converter de CSV para JSON: ");
        } catch (NumberFormatException e) {
            logger.log(Level.INFO, "Erro ao converter de CSV para JSON: formato de número inválido: ");
        }
    }

    /**

     This method receives a JSON file path and a CSV file path, reads the JSON file,
     and converts its content to CSV format. The CSV file is then saved in the provided path.

     The CSV format follows a specific structure with the following columns:
     Curso;Unidade Curricular;Turno;Turma;Inscritos no turno;Dia da semana;
     Hora início da aula;Hora fim da aula;Data da aula;Sala atribuída à aula;Lotação da sala

     The method throws an exception if there is any error while converting the file.

     @param jsonFilePath a string representing the path to the JSON file to be converted
     @param csvFilePath a string representing the path where the CSV file will be saved
     @throws Exception if there is any error while converting the file
     */
    public static void convertJSONtoCSV(String jsonFilePath, String csvFilePath) {
        try {
            Path path = Paths.get(jsonFilePath);
            String jsonContent = new String(Files.readAllBytes(path));
            JSONArray jsonArray = new JSONArray(jsonContent);
            try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
                csvWriter.append("Curso;Unidade Curricular;Turno;Turma;Inscritos no turno;Dia da semana;Hora início da aula;Hora fim da aula;Data da aula;Sala atribuída à aula;Lotação da sala\n");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject(UC);
                    String designacao = jsonObject.getString("Designação");
                    JSONArray cursoArray = jsonObject.getJSONArray("Curso");
                    StringBuilder curso = new StringBuilder();
                    for (int j = 0; j < cursoArray.length(); j++) {
                        curso.append(cursoArray.getString(j)).append(", ");
                    }
                    curso = new StringBuilder(curso.substring(0, curso.length() - 2));
                    JSONObject turno = jsonObject.getJSONObject("Turno");
                    int inscritos = turno.getInt("Inscritos no turno");
                    JSONArray turmaArray = turno.getJSONArray("Turma");
                    StringBuilder turma = new StringBuilder();
                    for (int k = 0; k < turmaArray.length(); k++) {
                        turma.append(turmaArray.getString(k)).append(", ");
                    }
                    turma = new StringBuilder(turma.substring(0, turma.length() - 2));
                    String idTurno = turno.getString("Id Turno");
                    String horaInicio = turno.getJSONObject(DATA).getString("Hora início da aula");
                    String horaFim = turno.getJSONObject(DATA).getString("Hora fim da aula");
                    String data = turno.getJSONObject(DATA).getString("Data");
                    String diaSemana = turno.getJSONObject(DATA).getString("Dia da semana");
                    String lotacaoSala = turno.getJSONObject(DATA).getJSONObject(SALA).getString("Lotação da sala");
                    String sala = turno.getJSONObject(DATA).getJSONObject(SALA).getString("Sala");
                    csvWriter.append(curso.toString()).append(";").append(designacao).append(";").append(idTurno).append(";").append(turma.toString()).append(";").append(Integer.toString(inscritos)).append(";").append(diaSemana).append(";").append(horaInicio).append(";").append(horaFim).append(";").append(data).append(";").append(sala).append(";").append(lotacaoSala).append("\n");
                }
                csvWriter.flush();
                logger.log(Level.INFO, "CSV file has been created successfully!");
            }

        } catch (Exception e) {
            logger.log(Level.INFO, "Error converting JSON to CSV:");
        }
    }

    /**

     This method loads a file from a given file path and returns a BufferedReader object to read the file's contents.

     @param filePath The path of the file to be loaded.
     @return A BufferedReader object to read the contents of the file
     */
    private static BufferedReader loadFile(String filePath) {
        if(filePath.startsWith("http")){
            try {
                URI uri = new URI(filePath);
                URL url = uri.toURL();
                return new BufferedReader(new InputStreamReader(url.openStream()));
            } catch (IOException | URISyntaxException e) {
                logger.log(Level.INFO, "Erro a ler o ficheiro");
            }
        } else {
            try {
                return new BufferedReader(new FileReader(filePath));
            } catch (FileNotFoundException e) {
                logger.log(Level.INFO, "Erro a ler o ficheiro");
            }
        }
        return null;
    }

    /**

     This method creates a new file with the given file path and returns a BufferedWriter object to write to the file.

     @param filePath The path of the file to be created.
     @return A BufferedWriter object to write to the new file.
     */
    private static BufferedWriter saveFile(String filePath) {
        if(filePath.startsWith("http") || filePath.startsWith("www")){
            try {
                URI uri = new URI(filePath);
                URL url = uri.toURL();
                return new BufferedWriter(new OutputStreamWriter(url.openConnection().getOutputStream()));
            } catch (IOException | URISyntaxException e) {
                logger.log(Level.INFO, "Erro a guardar o ficheiro");
            }
        } else {
            try {
                return new BufferedWriter(new FileWriter(filePath));
            } catch (IOException e) {
                logger.log(Level.INFO, "Erro a guardar o ficheiro");
            }
        }
        return null;
    }
}
