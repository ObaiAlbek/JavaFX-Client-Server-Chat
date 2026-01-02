package domain;

public enum UserInfo {
	VERFÜGBAR,BESCHÄFTIGT,IN_DER_SCHULE,IM_KINO,BEI_DER_ARBEIT,AKKU_FAST_LEER,SCHLAFE,
	CUSTOM; // Sonderfall für freie Texteingabe

    private String customValue;

    public void setCustomValue(String value) {
        if (this == CUSTOM) {
            this.customValue = value;
        }
    }

    public String getCustomValue() {
        return customValue;
    }
	
}
