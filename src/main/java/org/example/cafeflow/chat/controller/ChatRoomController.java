package org.example.cafeflow.chat.controller;

import org.example.cafeflow.chat.domain.ChatRoom;
import org.example.cafeflow.chat.dto.ChatRoomRequest;
import org.example.cafeflow.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/rooms")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @PostMapping
    public ChatRoom createRoom(@RequestBody ChatRoomRequest request) {
        return chatRoomService.createRoom(request.getCafeOwnerId(), request.getUserId());
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable("roomId") Long roomId) {
        chatRoomService.deleteRoom(roomId);
    }

    @GetMapping("/{roomId}")
    public ChatRoom getRoom(@PathVariable("roomId") Long roomId) {
        return chatRoomService.getRoom(roomId);
    }
}