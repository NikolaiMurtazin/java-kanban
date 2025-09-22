import model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpEpicsApiTest extends BaseHttpApiTest {

    @Test
    void createEpic_shouldReturn201() throws Exception {
        Epic e = new Epic("Move","Packing");
        var resp = post("/epics", gson.toJson(e));
        Assertions.assertEquals(201, resp.statusCode());
        Assertions.assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void updateEpic_nameDesc_only_shouldReturn200() throws Exception {
        Epic e = manager.createEpic(new Epic("Old","Desc"));
        e.setName("New");
        e.setDescription("NewDesc");
        var resp = post("/epics", gson.toJson(e));
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertEquals("New", manager.getEpicById(e.getId()).getName());
    }

    @Test
    void getEpic_unknown_should404() throws Exception {
        var resp = get("/epics?id=42");
        Assertions.assertEquals(404, resp.statusCode());
    }
}
