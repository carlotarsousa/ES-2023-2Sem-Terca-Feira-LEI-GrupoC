import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main{
    public static void main(String[] args) throws IOException {
        String inputFile = "C:\\Users\\gmigu\\Documents\\3ano\\2sem\\ES\\src\\horario_exemplo.csv";
        String outputFile = "output.json";

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line;
            JSONArray jsonArray = new JSONArray();

            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] columns = line.split(";", -1);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Curso", columns[0]);
                jsonObject.put("Unidade Curricular", columns[1]);
                jsonObject.put("Turno", columns[2]);
                jsonObject.put("Turma", columns[3]);
                jsonObject.put("Inscritos no turno", Integer.parseInt(columns[4]));
                jsonObject.put("Dia da semana", columns[5]);
                jsonObject.put("Hora início da aula", columns[6]);
                jsonObject.put("Hora fim da aula", columns[7]);
                jsonObject.put("Data da aula", columns[8]);
                jsonObject.put("Sala atribuída à aula", columns[9]);
                jsonObject.put("Lotação da sala", columns[10]);

                jsonArray.put(jsonObject);
            }

            FileWriter fw = new FileWriter(outputFile);
            fw.write(jsonArray.toString(4));
            fw.flush();
            fw.close();

            System.out.println("Arquivo JSON criado com sucesso: " + outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
