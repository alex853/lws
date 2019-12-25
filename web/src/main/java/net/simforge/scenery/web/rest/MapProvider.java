package net.simforge.scenery.web.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.simforge.commons.legacy.BM;
import net.simforge.commons.misc.RestUtils;
import net.simforge.refdata.aircraft.model.geo.Airport;
import net.simforge.scenery.core.LightWeightedScenery;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryObject;
import net.simforge.scenery.core.persistence.SceneryRevision;
import net.simforge.scenery.web.WebAppContext;
import net.simforge.scenery.web.dto.MarkerDto;
import net.simforge.scenery.web.dto.SceneryInfoDto;
import org.hibernate.Session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/map")
public class MapProvider {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private ServletContext servletContext;
    private WebAppContext webAppContext;

    @Context
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.webAppContext = WebAppContext.get(servletContext);
    }

    @Context
    public void setRequest(HttpServletRequest request) {
//        this.auth = new AuthHelper(request);
    }

    @GET
    @Path("/markers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMarkers() {
        BM.start("MapProvider.getMarkers");
        try (Session session = webAppContext.openSession()) {
            Map<Integer, MarkerDto> airportIdToMarker = new HashMap<>();

            //noinspection JpaQlInspection,unchecked
            List<Scenery> sceneryList = session
                    .createQuery("from Scenery")
                    .list();

            for (Scenery scenery : sceneryList) {
                SceneryRevision revision = LightWeightedScenery.loadLastPublishedRevision(session, scenery);

                if (revision == null) {
                    continue;
                }

                List<SceneryObject> objects = LightWeightedScenery.loadObjects(session, revision);
                for (SceneryObject object : objects) {
                    switch (object.getType()) {
                        case SceneryObject.Type.Airport:
                            Airport airport = object.getAirport();
                            MarkerDto airportMarker = airportIdToMarker.get(airport.getId());
                            if (airportMarker == null) {
                                airportMarker = new MarkerDto();
                                airportMarker.setType("airport");
                                airportMarker.setIcao(airport.getIcao());
                                airportMarker.setName(airport.getName());
                                airportMarker.setLat(airport.getLatitude());
                                airportMarker.setLon(airport.getLongitude());
                                airportIdToMarker.put(airport.getId(), airportMarker);
                            }
                            airportMarker.addId(String.valueOf(scenery.getId()));
                            break;

                        default:
                            // todo warn
                            break;
                    }
                }
            }

            return Response.ok(RestUtils.success(airportIdToMarker.values())).build();
        } finally {
            BM.stop();
        }
    }

    @POST
    @Path("/scenery-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSceneryInfo(@FormParam("ids") String sceneryIdsJson) {
        BM.start("MapProvider.getSceneryInfo");
        try (Session session = webAppContext.openSession()) {
            List<SceneryInfoDto> sceneryInfos = new ArrayList<>();

            List<Integer> sceneryIds = new Gson().fromJson(sceneryIdsJson, new TypeToken<List<Integer>>() {}.getType());

            for (Integer sceneryId : sceneryIds) {

                Scenery scenery = session.load(Scenery.class, sceneryId);
                SceneryRevision publishedRevision = LightWeightedScenery.loadLastPublishedRevision(session, scenery);

                SceneryInfoDto dto = new SceneryInfoDto();
                dto.setId(String.valueOf(scenery.getId()));
                dto.setSceneryTitle(scenery.getTitle());
                dto.setSceneryAuthors(scenery.getAuthors());
                dto.setRevisionNumber(publishedRevision != null ? String.valueOf(publishedRevision.getRevNumber()) : "N/A");

                sceneryInfos.add(dto);
            }

            return Response.ok(RestUtils.success(sceneryInfos)).build();
        } finally {
            BM.stop();
        }
    }
}
