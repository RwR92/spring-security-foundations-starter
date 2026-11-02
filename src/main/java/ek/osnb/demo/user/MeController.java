package ek.osnb.demo.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/me")
public class MeController {

    record MeResponse(String username, List<String> roles) {
    }

    @GetMapping
    MeResponse me (Authentication authentication) {
        String name = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .filter(a -> a.startsWith("ROLE_"))
                .map(s -> s.replace("ROLE_", ""))
                .toList();

        return new MeResponse(name, roles);
    }

}

/*
 * 1. Where does Spring Security store the authenticated user's identity?
 *
 *    During a request: in the SecurityContextHolder, which by default uses a
 *    ThreadLocal, so each request thread has its own isolated identity.
 *    Between requests: after login, the SecurityContext is saved server-side in
 *    the HttpSession (key "SPRING_SECURITY_CONTEXT"). The browser only gets the
 *    session id in the JSESSIONID cookie.
 *    On each request, the filter chain uses the cookie to load the
 *    SecurityContext from the session into the SecurityContextHolder, and clears
 *    it when the request ends. The identity itself never leaves the server,
 *    so the client cannot forge it.
 *
 * 2. What is the purpose of SecurityContext?
 *
 *    It is the container that holds the current user's Authentication
 *    (principal/username, credentials and authorities/roles).
 *    It gives one standard place to answer "who is the current user", so
 *    filters, controllers and services all read the same identity without
 *    passing the user around manually.
 *    It also decouples authentication from the rest of the app: form login,
 *    HTTP Basic or JWT all end up putting an Authentication into the
 *    SecurityContext, so code like authentication.getName() works regardless
 *    of how the user logged in.
 *
 * 3. Why should the backend determine the note owner rather than trusting the frontend?
 *
 *    The frontend runs on the client and can be manipulated (DevTools, curl,
 *    Postman), so all request data is untrusted input. If the owner came from
 *    the request, a user could send owner=admin and create notes in someone
 *    else's name (identity spoofing / Broken Access Control, OWASP #1).
 *    The only trustworthy source of identity is the server-side SecurityContext,
 *    established by verifying the user's password at login.
 *    Rule: trust the request for WHAT the user wants (the text, after
 *    validation), but never for WHO the user is.
 */

/*
 * 1. Does JavaScript need to add the session cookie manually to every request?
 *
 *    No. The browser stores the JSESSIONID cookie from Set-Cookie and attaches
 *    it automatically to every request to that domain. fetch() sends cookies by
 *    default for same-origin requests (credentials: "same-origin").
 *    Cross-origin requests require credentials: "include" on the client and
 *    CORS with Access-Control-Allow-Credentials: true on the server.
 *    Downside: the browser also attaches the cookie to requests started by other
 *    sites, which is what CSRF exploits.
 *
 * 2. What does the HttpOnly flag do?
 *
 *    It makes the cookie inaccessible to JavaScript (not visible in
 *    document.cookie), while the browser still sends it with requests.
 *    It protects against session hijacking via XSS: an injected script cannot
 *    steal the session id and send it to an attacker.
 *    It does not prevent XSS itself; injected scripts can still send requests
 *    from the victim's browser. Tomcat sets HttpOnly on JSESSIONID by default.
 *
 * 3. How does SameSite affect cross-site requests?
 *
 *    It controls whether the cookie is sent on requests started by another site:
 *    - Strict: never sent cross-site.
 *    - Lax: sent only on top-level GET navigations (clicking a link), not on
 *      cross-site POST, fetch, iframes or images. Default in modern browsers
 *      when SameSite is not set.
 *    - None: always sent; requires Secure (HTTPS).
 *    Lax/Strict block the classic CSRF attack (a hidden cross-site form POST),
 *    because the session cookie is not attached. It is defense in depth, not a
 *    full replacement for CSRF tokens (old browsers, cross-site GET, subdomains).
 */