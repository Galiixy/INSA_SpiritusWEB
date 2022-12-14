package serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mycompany.spiritus.metier.model.*;
import com.mycompany.spiritus.metier.service.AccountService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class RequestConsultationSerialization extends Serialization {

    @Override
    public void serialize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        if ( (int)request.getAttribute("status") != HttpServletResponse.SC_OK) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", (String)request.getAttribute("message"));
        } else {
            jsonObject.addProperty("success", true);
            Consultation consultation = (Consultation)request.getAttribute("consultation");
            Medium medium = consultation.getMedium();

            JsonObject consultationJson = new JsonObject();
            JsonObject mediumJson = new JsonObject();

            String formattedDate = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            formattedDate = sdf.format(consultation.getDate());

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("clientId", consultation.getClient().getId());

            mediumJson.addProperty("denomination", medium.getDenomination());
            mediumJson.addProperty("presentation", medium.getPresentation());
            mediumJson.addProperty("gender", medium.getGender().name());
            mediumJson.addProperty("id", medium.getId());

            if (medium instanceof Spirite) {
                mediumJson.addProperty("type", "spirite");
                mediumJson.addProperty("support", ((Spirite) medium).getSupport());
            } else if (medium instanceof Astrologue) {
                Astrologue astrologue = (Astrologue) medium;
                mediumJson.addProperty("type", "astrologue");
                mediumJson.addProperty("formation", astrologue.getFormation());
                mediumJson.addProperty("promotion", astrologue.getPromotion());
            } else {
                mediumJson.addProperty("type", "cartomancien");
            }

            consultationJson.addProperty("date", formattedDate);
            consultationJson.addProperty("status", consultation.getStatus().name());
            consultationJson.addProperty("advisor", consultation.getEmployee().getLastName());
            consultationJson.add("medium", mediumJson);

            jsonObject.add("consultation", consultationJson);

        }

        System.out.println("response status consultation request : " + (int)request.getAttribute("status"));
        response.setStatus((int)request.getAttribute("status"));
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        gson.toJson(jsonObject, out);
        out.close();
    }

}
