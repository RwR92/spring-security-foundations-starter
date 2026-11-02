export async function getMe() {
    console.log("hej mig")
    // TODO Exercise 6:
    const res = await fetch("/api/me");

    if(!res.ok){
        return null;
    }
    return await res.json();
    // Return null if the status is 401
    // Otherwise return the user data
    // throw new Error("getMe is not implemented yet");
}

export async function login(formData) {
    // TODO Exercise 6:
    const res = await fetch("/api/login", {
        method: "POST",
        body: new URLSearchParams(formData),
        headers: {
            "X-XSRF-TOKEN": csrfToken()
        }
    });
    if (!res.ok) {
        throw new Error("Login failed");
    }
    // POST /api/login
    // Send username and password as form data
    // Include the CSRF token
    // throw new Error("login is not implemented yet");
}

export async function logout() {
    // TODO Exercise 6:
    const res = await fetch("/api/logout", {
        method: "POST",
        headers: {
            "X-XSRF-TOKEN": csrfToken()
        }
    });
    if (!res.ok) {
        throw new Error("An error occurred while logging out");
    }
    // POST /api/logout
    // Include the CSRF token
}

export async function getNotes(url = "/api/notes") {
    const response = await fetch(url);

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return response.json();
}

// TODO Exercise 5: Send the CSRF token in the X-XSRF-TOKEN header
export async function addNote(formData) {
    const response = await fetch("/api/notes", {
        method: "POST",
        body: JSON.stringify(
            {"text": formData.get("text")}
        ),
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken()
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }
}


function csrfToken() {
    // TODO Exercise 5:
    const cookieJar = document.cookie.split('; ');
    const xsrfCookie = cookieJar.find(c => c.startsWith("XSRF-TOKEN="));

    if (!xsrfCookie) {
        return null;
    }

    const token = xsrfCookie.split("=")[1];

    return decodeURIComponent(token);
    // Read XSRF-TOKEN from document.cookie
    // Decode the value
    // Return the token
}
