package anas.kazay.chatapp.controller;

import anas.kazay.chatapp.dto.FavoriteRequest;
import anas.kazay.chatapp.model.Favorite;
import anas.kazay.chatapp.model.User;
import anas.kazay.chatapp.service.FavoriteService;
import anas.kazay.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    public FavoriteController(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestBody FavoriteRequest request) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(favoriteService.addFavorite(currentUser, request));
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getUserFavorites() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(favoriteService.getUserFavorites(currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        favoriteService.removeFavorite(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}