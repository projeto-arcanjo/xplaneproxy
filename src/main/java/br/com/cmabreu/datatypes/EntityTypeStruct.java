package br.com.cmabreu.datatypes;


public class EntityTypeStruct {
	private byte entityKind;
	private byte domain;
	private short countryCode;
	private byte category;
	private byte subCategory;
	private byte specific;
	private byte extra;        
	
    public EntityTypeStruct(int a, int b , int c, int d, int e, int f, int g){
    	entityKind = (byte) a;
    	domain =(byte) b;
    	countryCode =(byte) c;
    	category =(byte) d;
    	subCategory =(byte) e;
    	specific = (byte)f;
    	extra = (byte)g;
    }    
    
	public byte getentityKind() {
		return entityKind;
	}

	public void setentityKind(byte entityKind) {
		this.entityKind = entityKind;
	}

	public byte getdomain() {
		return domain;
	}

	public void setdomain(byte domain) {
		this.domain = domain;
	}

	public short getcountryCode() {
		return countryCode;
	}

	public void setcountryCode(short countryCode) {
		this.countryCode = countryCode;
	}

	public byte getcategory() {
		return category;
	}

	public void setcategory(byte category) {
		this.category = category;
	}

	public byte getsubCategory() {
		return subCategory;
	}

	public void setsubCategory(byte subCategory) {
		this.subCategory = subCategory;
	}

	public byte getspecific() {
		return specific;
	}

	public void setspecific(byte specific) {
		this.specific = specific;
	}

	public byte getextra() {
		return extra;
	}

	public void setextra(byte extra) {
		this.extra = extra;
	}
	
}
