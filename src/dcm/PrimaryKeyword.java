package dcm;

enum PrimaryKeyword {
	
	END,
	FESTWERT, //PARAMETER
	FESTWERTEBLOCK, //MATRIX
	KENNLINIE, //LINE
	KENNFELD, //MAP
	FESTKENNLINIE, //FIXED_LINE
	FESTKENNFELD, //FIXED_MAP
	GRUPPENKENNLINIE, //GROUP_LINE
	GRUPPENKENNFELD, //GROUP_MAP
	STUETZSTELLENVERTEILUNG, //DISTRIBUTION
	TEXTSTRING, //TEXTSTRING
	UNKNOWN;
	
	public static PrimaryKeyword getPrimaryKeyword(String type) {
        switch (type) {
        case "END":
            return END;
        case "FESTWERT":
            return FESTWERT;
        case "FESTWERTEBLOCK":
            return FESTWERTEBLOCK;
        case "KENNLINIE":
            return KENNLINIE;
        case "KENNFELD":
            return KENNFELD;
        case "FESTKENNLINIE":
            return FESTKENNLINIE;
        case "FESTKENNFELD":
            return FESTKENNFELD;
        case "GRUPPENKENNLINIE":
            return GRUPPENKENNLINIE;
        case "GRUPPENKENNFELD":
            return GRUPPENKENNFELD;
        case "STUETZSTELLENVERTEILUNG":
            return STUETZSTELLENVERTEILUNG;
        case "TEXTSTRING":
            return TEXTSTRING;
        default:
            return UNKNOWN;
        }

    }
	
}
