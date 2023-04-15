import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main{
    public static void main(String[] args) throws IOException {
        String inputFile = "src/horario_exemplo.csv";
        String outputFile = "output.json";
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
                sala.put("Sala atribuída à aula", columns[9]);
                sala.put("Lotação da sala", columns[10]);
                JSONObject data = new JSONObject();
                data.put("Data da aula", columns[8]);
                data.put("Dia da semana", columns[5]);
                data.put("Hora início da aula", columns[6]);
                data.put("Hora fim da aula", columns[7]);
                data.put("Sala atribuída à aula", sala);
                JSONObject turno = new JSONObject();
                turno.put("Turno", columns[2]);
                turno.put("Turma", columns[3]);
                turno.put("Inscritos no turno", Integer.parseInt(columns[4]));
                turno.put("Data da aula", data);
                JSONObject unidadeCurricular = new JSONObject();
                unidadeCurricular.put("Unidade Curricular", columns[1]);
                unidadeCurricular.put("Curso", columns[0]);
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
            throw new RuntimeException(e);
        }
    }
}
