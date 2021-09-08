package edu.escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HttpServer {
    private static final HttpServer _instance = new HttpServer();
    public static HttpServer getInstance(){return _instance;}
    private String ApiKey ="&appid=eb688602f7038fa22c9ef6cfef62d07a";
    private String URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    public void start(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running=true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            serveConneciton(clientSocket);
        }
        serverSocket.close();
    }
    public void serveConneciton(Socket clientSocket) throws IOException {
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        ArrayList<String> request = new ArrayList<String>();
        while ((inputLine = bufferedReader.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            request.add(inputLine);
            if (!bufferedReader.ready()) {
                break;
            }
        }
        String UrlStr="";
        if(request.size()>0) {
            UrlStr = request.get(0).split(" ")[1];
        }
        if (UrlStr.equals("/clima")){
            outputLine=Clima();
            printWriter.println(outputLine);
        }
        else if(UrlStr.contains("/consulta?lugar=")){
            String site = URL+UrlStr.replace("/consulta?lugar=","")+ApiKey;
            outputLine=Json(site);
            System.out.println(outputLine);
            printWriter.println(outputLine);
        }

    }

    public String Clima(){
        String outputline="HTTP/1.1 200 OK\r\n"
                + "Content - Type: text/javascript\r\n"
                + "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\" integrity=\"sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO\" crossorigin=\"anonymous\">\n" +
                "    <title>Clima</title>\n" +
                "</head>\n" +
                "<script>\n" +
                "    function Consultar(){\n" +
                "        if(window.miVentana){\n" +
                "            miVentana.close();\n" +
                "        }\n" +
                "        miVentana = window.open(\"consulta\"+\"?lugar=\"+document.getElementById(\"Ciudad\").value, \"ventana1\", \"height=screen.height,width=screen.width,left=300,location=yes,menubar=no,resizable=no,scrollbars=yes,status=no,titlebar=yes,top=300\");\n" +
                "    }\n" +
                "</script>\n" +
                "<center>\n" +
                "    <body background=\"https://github.com/RichardUG/SparkHerokuApp/blob/master/img/wallper.png?raw=true\">\n" +
                "        <br><br><br>\n" +
                "        Ingrese la ciudad a consultar: <input type=\"Ciudad\" name=\"Ciudad\" id=\"Ciudad\"/><br><br>\n" +
                "        <button class=\"btn btn-primary\" onclick=\"Consultar()\" >Consultar</button>\n" +
                "        <script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>\n" +
                "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\"></script>\n" +
                "        <script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js\" integrity=\"sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy\" crossorigin=\"anonymous\"></script>\n" +
                "    </body>\n" +
                "</center>\n" +
                "</html>";
        return outputline;
    }

    public String Json(String site) throws IOException{
        String inputLine = null;
        StringBuffer JSON = new StringBuffer();
        URL siteURL = new URL(site);
        URLConnection urlConnection = siteURL.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            while ((inputLine = reader.readLine()) != null) {
                JSON.append(inputLine);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return JSON.toString();
    }

    public int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
