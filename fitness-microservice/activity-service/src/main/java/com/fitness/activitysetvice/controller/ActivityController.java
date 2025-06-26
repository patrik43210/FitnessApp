package com.fitness.activitysetvice.controller;

import com.fitness.activitysetvice.dto.ActivityRequest;
import com.fitness.activitysetvice.dto.ActivityResponse;
import com.fitness.activitysetvice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping("/track-activity")
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request){

        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping("/allActivities")
    public ResponseEntity<List<ActivityResponse>> getAllActivities(){

        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/user-activities")
    public ResponseEntity<List<ActivityResponse>> getActivitiesForUser(@RequestHeader("X-User-ID") String userId){

        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId){

        return ResponseEntity.ok(activityService.getActivity(activityId));
    }
}
