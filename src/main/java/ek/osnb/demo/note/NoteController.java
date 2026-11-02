package ek.osnb.demo.note;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
class NoteController {

    private final NoteRepository notes;

    NoteController(NoteRepository notes) {
        this.notes = notes;
    }

    // TODO Exercise 3: Return only the notes owned by the logged-in user
    @GetMapping("/api/notes")
    List<Note> myNotes(Authentication authentication) {
        return notes.findByOwner(authentication.getName());
    }


    record CreateNoteRequest(String text){
    }
    // TODO Exercise 3: Save the note with the logged-in user as its owner
    // A form post (text=...), just like the login form
    @PostMapping("/api/notes")
    @ResponseStatus(HttpStatus.CREATED)
    Note addNote(@RequestBody CreateNoteRequest request, Authentication authentication) {
        Note note = new Note(request.text(), authentication.getName());
        return notes.save(note);
    }

    @GetMapping("/api/admin/notes")
    List<Note> allNotes() {
        return notes.findAll();
    }
}
