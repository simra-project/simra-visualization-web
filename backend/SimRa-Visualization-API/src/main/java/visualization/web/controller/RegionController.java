package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.data.mongodb.entities.RegionEntity;
import visualization.service.RegionService;
import visualization.web.resources.RegionResource;

import javax.validation.Valid;
import java.util.List;


/*⁄

This is the place where we communicate with the frontend

 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RegionController {

    @Autowired
    private RegionService regionService;


    @GetMapping(value = "/regions")
    public HttpEntity<List<RegionResource>> getRegions() {
        return ResponseEntity.ok(regionService.getRegions());
    }

    @PostMapping(value = "/regions")
    public HttpEntity<RegionResource> createRegion(@Valid @RequestBody RegionEntity regionEntity) {
        return ResponseEntity.ok(regionService.createRegion(regionEntity));
    }
}
