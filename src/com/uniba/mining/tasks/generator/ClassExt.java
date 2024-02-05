package com.uniba.mining.tasks.generator;

import com.vp.plugin.model.IClass;


public class ClassExt {

	private Boolean optional;
	private IClass classe;
	
	

	public ClassExt(Boolean optional, IClass classe) {
		super();
		this.optional = optional;
		this.classe=classe;
	}


	public Boolean getOptional() {
		return optional;
	}
	
	public IClass getClasse() {
		return classe;
	}


}
