package org.openweather.domain;

public record Message(
        String role,     // "system" | "user" | "assistant"
        String content
) {
    public static Message system(String c)    { return new Message("system", c); }
    public static Message user(String c)      { return new Message("user", c); }
    public static Message assistant(String c) { return new Message("assistant", c); }
}
