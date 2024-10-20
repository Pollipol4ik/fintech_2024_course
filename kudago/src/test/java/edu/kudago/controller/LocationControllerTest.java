package edu.kudago.controller;

import edu.kudago.dto.Location;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    private Location location1;
    private Location location2;

    @BeforeEach
    public void setUp() {
        location1 = new Location("moscow", "Moscow");
        location2 = new Location("spb", "Saint Petersburg");
    }

    @Test
    @DisplayName("Get all locations - success")
    public void testGetAllLocations() throws Exception {
        Mockito.when(locationService.getAllLocations()).thenReturn(List.of(location1, location2));

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"slug\":\"moscow\",\"name\":\"Moscow\"},{\"slug\":\"spb\",\"name\":\"Saint Petersburg\"}]"));
    }

    @Test
    @DisplayName("Get location by ID - success")
    public void testGetLocationBySlug() throws Exception {
        Mockito.when(locationService.getLocationBySlug("moscow")).thenReturn(location1);

        mockMvc.perform(get("/api/v1/locations/{slug}", "moscow"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"slug\":\"moscow\",\"name\":\"Moscow\"}"));
    }

    @Test
    @DisplayName("Get location by non-existent ID - failure")
    public void testGetLocationBySlugNotFound() throws Exception {
        Mockito.when(locationService.getLocationBySlug("unknown")).thenThrow(new ResourceNotFoundException("Location not found with slug: unknown"));

        mockMvc.perform(get("/api/v1/locations/{slug}", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Location not found with slug: unknown\"}"));
    }

    @Test
    @DisplayName("Create location - success")
    public void testCreateLocation() throws Exception {
        Mockito.when(locationService.createLocation(any(Location.class))).thenReturn(location1);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"moscow\",\"name\":\"Moscow\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"slug\":\"moscow\",\"name\":\"Moscow\"}"));
    }

    @Test
    @DisplayName("Update location - success")
    public void testUpdateLocation() throws Exception {
        Mockito.when(locationService.updateLocation(eq("moscow"), any(Location.class))).thenReturn(location1);

        mockMvc.perform(put("/api/v1/locations/{slug}", "moscow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"moscow\",\"name\":\"Moscow\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"slug\":\"moscow\",\"name\":\"Moscow\"}"));
    }

    @Test
    @DisplayName("Update non-existent location - failure")
    public void testUpdateLocationNotFound() throws Exception {
        Mockito.when(locationService.updateLocation(eq("unknown"), any(Location.class)))
                .thenThrow(new ResourceNotFoundException("Location not found with slug: unknown"));

        mockMvc.perform(put("/api/v1/locations/{slug}", "unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"unknown\",\"name\":\"Unknown\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Location not found with slug: unknown\"}"));
    }

    @Test
    @DisplayName("Delete location - success")
    public void testDeleteLocation() throws Exception {
        Mockito.doNothing().when(locationService).deleteLocation("moscow");

        mockMvc.perform(delete("/api/v1/locations/{slug}", "moscow"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete non-existent location - failure")
    public void testDeleteLocationNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Location not found with slug: unknown")).when(locationService).deleteLocation("unknown");

        mockMvc.perform(delete("/api/v1/locations/{slug}", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Location not found with slug: unknown\"}"));
    }
}
