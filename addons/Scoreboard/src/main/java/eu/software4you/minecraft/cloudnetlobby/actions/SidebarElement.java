package eu.software4you.minecraft.cloudnetlobby.actions;

public class SidebarElement {
    private final String prefix;
    private final String body;
    private final String suffix;

    public SidebarElement(String prefix, String body, String suffix) {
        this.prefix = prefix;
        this.body = body;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getBody() {
        return body;
    }

    public String getSuffix() {
        return suffix;
    }
}
