package com.example.Dynamo_Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Dynamo_Backend.dto.GroupDto;
import com.example.Dynamo_Backend.service.GroupService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {
    public final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        List<GroupDto> groups = groupService.getGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @PostMapping
    public ResponseEntity<GroupDto> addGroup(@RequestBody GroupDto groupDto) {
        GroupDto group = groupService.addGroup(groupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);

    }

    @PutMapping("/{group_id}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable("group_id") String Id,
            @RequestBody GroupDto groupDto) {
        GroupDto updateGroups = groupService.updateGroup(Id, groupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateGroups);
    }

    @DeleteMapping("/{group_id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("group_id") String Id) {
        groupService.deleteGroup(Id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{group_id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable("group_id") String Id) {
        GroupDto group = groupService.getGroupById(Id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/status")
    public ResponseEntity<List<GroupDto>> getAllGroupStatus() {
        List<GroupDto> groups = groupService.getStaffStatusGroup();
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

}
