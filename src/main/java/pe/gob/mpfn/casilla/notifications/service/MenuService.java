package pe.gob.mpfn.casilla.notifications.service;

import org.springframework.stereotype.Service;
import pe.gob.mpfn.casilla.notifications.util.enums.Folder;
import pe.gob.mpfn.casilla.notifications.util.enums.Tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuService {


    public Map<String, List<HashMap<String, Object>>> getMenu() {

        var folders = Arrays
                .stream(Folder.values())
                .map(folder -> {
                    var map1 = new HashMap<String, Object>();
                    map1.put("id", folder.getKey());
                    map1.put("name", folder.getValue());
                    return map1;
                }).toList();

        var tags = Arrays.stream(Tag.values())
                .map(tag -> {
                    var map1 = new HashMap<String, Object>();
                    map1.put("id", tag.getKey());
                    map1.put("name", tag.getValue());
                    return map1;
                }).toList();

        return Map.of("folders", folders, "tags", tags);
    }
}
