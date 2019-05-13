package cdf;

public enum TypeVariable {
	
	ASCII,
	VALUE,
	CURVE,
	MAP,
	VAL_BLK,
	COM_AXIS,
	SW_COMPONENT,
	UNKNOWN;
	
	
	public final static TypeVariable getType(String name)
	{
		switch (name) {
		case "ASCII":
			return ASCII;
		case "VALUE":
			return VALUE;
		case "CURVE":
			return CURVE;
		case "CURVE_INDIVIDUAL":
			return CURVE;
		case "CURVE_FIXED":
			return CURVE;
		case "CURVE_GROUPED":
			return CURVE;
		case "MAP":
			return MAP;
		case "MAP_INDIVIDUAL":
			return MAP;
		case "MAP_FIXED":
			return MAP;
		case "MAP_GROUPED":
			return MAP;
		case "VAL_BLK":
			return VAL_BLK;
		case "VALUE_BLOCK":
			return VAL_BLK;
		case "AXIS_VALUES":
			return COM_AXIS;
		case "SW_COMPONENT":
			return SW_COMPONENT;
		default:
			return UNKNOWN;
		}
	}

}
