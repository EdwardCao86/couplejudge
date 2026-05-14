package com.catjudge.couplejudge.model;

public class JudgeRole {
    private final String type;
    private final String name;
    private final String style;
    private final String emoji;
    private final String personaPrompt;

    public JudgeRole(String type, String name, String style, String emoji, String personaPrompt) {
        this.type = type;
        this.name = name;
        this.style = style;
        this.emoji = emoji;
        this.personaPrompt = personaPrompt;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getPersonaPrompt() {
        return personaPrompt;
    }
}
