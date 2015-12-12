package com.miranda.luis.activity_chat;

public class ItemCompra {
	protected long id;
	protected String rutaImagen;
	protected String status;
	protected String nombre;
	protected String tipo;
	
	
	public ItemCompra() {
		this.nombre = "";
		this.tipo = "";
		this.rutaImagen = "";
		this.status = "";
	}
	
	public ItemCompra(long id, String nombre, String tipo) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaImagen = "";
		this.status = "";
	}
	
	public ItemCompra(long id, String nombre, String tipo, String rutaImagen, String status ) {
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaImagen = rutaImagen;
		this.status = status;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getRutaImagen() {
		return rutaImagen;
	}

	public String getStatus() {
		return status;
	}
	
	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
