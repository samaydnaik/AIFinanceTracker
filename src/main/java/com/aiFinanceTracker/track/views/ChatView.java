package com.aiFinanceTracker.track.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.web.client.RestClient;

@Route("chat")
@AnonymousAllowed
public class ChatView extends VerticalLayout {

    private final RestClient restClient;

    private final Div messagesContainer = new Div();
    private final TextArea input = new TextArea("Your message");
    private final Button send = new Button("Send");

    private String sessionId = null;

    public ChatView() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080") // adjust if needed
                .build();

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        messagesContainer.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "0.75rem")
                .set("overflow", "auto")
                .set("flex", "1");

        input.setWidthFull();
        input.setMaxHeight("150px");

        send.addClickListener(e -> sendMessage());

        VerticalLayout bottom = new VerticalLayout(input, send);
        bottom.setWidthFull();
        bottom.setSpacing(false);
        bottom.setPadding(false);
        bottom.setAlignItems(FlexComponent.Alignment.END);

        add(messagesContainer, bottom);
        setFlexGrow(1, messagesContainer); // chat area grows[web:88][web:90]
    }

    private void sendMessage() {
        String text = input.getValue();
        if (text == null || text.isBlank()) {
            return;
        }

        appendMessage("You", text);
        input.clear();

        ChatRequest req = new ChatRequest();
        req.sessionId = sessionId;
        req.message = text;

        ChatResponse res = restClient
                .post()
                .uri("/api/ai/chat")
                .body(req)
                .retrieve()
                .body(ChatResponse.class);

        if (res != null) {
            if (sessionId == null || "new".equals(res.sessionId)) {
                sessionId = res.sessionId;
            }
            appendMessage("AI", res.reply);
        }
    }

    private void appendMessage(String who, String text) {
        Div bubble = new Div();
        bubble.getStyle()
                .set("margin-bottom", "0.5rem")
                .set("padding", "0.5rem 0.75rem")
                .set("border-radius", "8px")
                .set("max-width", "80%");

        if ("You".equals(who)) {
            bubble.getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)")
                    .set("margin-left", "auto");
        } else {
            bubble.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("margin-right", "auto");
        }

        Span header = new Span(who + ":");
        header.getStyle().set("font-weight", "600");

        Div body = new Div();
        body.setText(text);

        bubble.add(header, body);
        messagesContainer.add(bubble);
        messagesContainer.getElement().callJsFunction("scrollIntoView", true);
    }

    // DTOs to match ChatAiController
    public static class ChatRequest {
        public String sessionId;
        public String message;
    }

    public static class ChatResponse {
        public String sessionId;
        public String reply;
    }
}