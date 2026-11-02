import { createLogin } from "./components/Login.js";
import { createNotes } from "./components/Notes.js";
import { createUserInfo } from "./components/UserInfo.js";
import { getMe } from "./api.js";

window.addEventListener("DOMContentLoaded", render);


async function render() {
    const app = document.querySelector("#app");

    app.replaceChildren();

    try {
        // TODO Exercise 6:
        const me = await getMe();

        if(!me){
            app.append(createLogin(render));
            return;
        }

        // Call getMe()
        // Not logged in: show only createLogin(render)
        // Logged in: also show createUserInfo(me, render), and pass me to createNotes(me)

        const heading = document.createElement("h1");
        heading.textContent = "My Notes";

        app.append(
            heading,
            createUserInfo(me,render),
            createNotes()
        );

    } catch (error) {
        app.textContent = "Could not load application.";
        console.error(error);
    }
}
