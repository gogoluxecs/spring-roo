package org.springframework.roo.classpath.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;

/**
 * Builder for {@link ClassOrInterfaceTypeDetails}.
 * 
 * @author Ben Alex
 * @since 1.1
 */
public class ClassOrInterfaceTypeDetailsBuilder extends AbstractMemberHoldingTypeDetailsBuilder<ClassOrInterfaceTypeDetails> {
	
	// Fields
	private JavaType name;
	private PhysicalTypeCategory physicalTypeCategory;
	private ClassOrInterfaceTypeDetailsBuilder superclass;
	private final List<JavaSymbolName> enumConstants = new ArrayList<JavaSymbolName>();
	private final Set<ImportMetadata> registeredImports = new HashSet<ImportMetadata>();

	/**
	 * Constructor
	 *
	 * @param declaredbyMetadataId
	 */
	public ClassOrInterfaceTypeDetailsBuilder(String declaredbyMetadataId) {
		super(declaredbyMetadataId);
	}

	/**
	 * Constructor
	 *
	 * @param existing
	 */
	public ClassOrInterfaceTypeDetailsBuilder(ClassOrInterfaceTypeDetails existing) {
		super(existing);
		init(existing);
	}

	/**
	 * Constructor
	 *
	 * @param declaredbyMetadataId
	 * @param existing
	 */
	public ClassOrInterfaceTypeDetailsBuilder(String declaredbyMetadataId, ClassOrInterfaceTypeDetails existing) {
		super(declaredbyMetadataId, existing);
		init(existing);
	}

	/**
	 * Constructor
	 *
	 * @param declaredbyMetadataId
	 * @param modifier
	 * @param name
	 * @param physicalTypeCategory
	 */
	public ClassOrInterfaceTypeDetailsBuilder(String declaredbyMetadataId, int modifier, JavaType name, PhysicalTypeCategory physicalTypeCategory) {
		this(declaredbyMetadataId);
		setModifier(modifier);
		this.name = name;
		this.physicalTypeCategory = physicalTypeCategory;
	}
	
	private void init(ClassOrInterfaceTypeDetails existing) {
		this.name = existing.getName();
		this.physicalTypeCategory = existing.getPhysicalTypeCategory();
		if (existing.getSuperclass() != null) {
			this.superclass = new ClassOrInterfaceTypeDetailsBuilder(existing.getSuperclass());
		}
		this.enumConstants.addAll(existing.getEnumConstants());
		this.registeredImports.clear();
		this.registeredImports.addAll(existing.getRegisteredImports());
	}

	public JavaType getName() {
		return name;
	}

	public void setName(JavaType name) {
		this.name = name;
	}

	public PhysicalTypeCategory getPhysicalTypeCategory() {
		return physicalTypeCategory;
	}

	public void setPhysicalTypeCategory(PhysicalTypeCategory physicalTypeCategory) {
		this.physicalTypeCategory = physicalTypeCategory;
	}

	public ClassOrInterfaceTypeDetailsBuilder getSuperclass() {
		return superclass;
	}

	public void setSuperclass(ClassOrInterfaceTypeDetailsBuilder superclass) {
		this.superclass = superclass;
	}

	public List<JavaSymbolName> getEnumConstants() {
		return enumConstants;
	}

	/**
	 * Sets this builder's enum constants to the given collection
	 * 
	 * @param enumConstants can be <code>null</code> for none, otherwise is
	 * defensively copied
	 */
	public void setEnumConstants(final Collection<? extends JavaSymbolName> enumConstants) {
		this.enumConstants.clear();
		if (enumConstants != null) {
			this.enumConstants.addAll(enumConstants);
		}
	}

	public boolean addEnumConstant(JavaSymbolName javaSymbolName) {
		return enumConstants.add(javaSymbolName);
	}

	public ClassOrInterfaceTypeDetails build() {
		ClassOrInterfaceTypeDetails superclass = null;
		if (this.superclass != null) {
			superclass = this.superclass.build();
		}
		return new DefaultClassOrInterfaceTypeDetails(getCustomData().build(), getDeclaredByMetadataId(), getModifier(), buildAnnotations(), getName(), getPhysicalTypeCategory(), buildConstructors(), buildFields(), buildMethods(), buildInnerTypes(), buildInitializers(), superclass, getExtendsTypes(), getImplementsTypes(), getEnumConstants(), getRegisteredImports());
	}

	/**
	 * Returns this builder's imports
	 * 
	 * @return a non-<code>null</code> copy
	 */
	public Set<ImportMetadata> getRegisteredImports() {
		return new HashSet<ImportMetadata>(registeredImports);
	}

	/**
	 * Sets this builder's imports
	 * 
	 * @param registeredImports can be <code>null</code> for none; defensively copied
	 */
	public void setRegisteredImports(final Collection<ImportMetadata> registeredImports) {
		this.registeredImports.clear();
		if (registeredImports != null) {
			this.registeredImports.addAll(registeredImports);
		}
	}
	
	/**
	 * Adds the given import to this builder
	 * 
	 * @param importMetadata the import to add; can be <code>null</code> not to
	 * add anything
	 * @return <code>true</code> if the state of this builder changed
	 */
	public boolean add(final ImportMetadata importMetadata) {
		if (importMetadata == null) {
			return false;
		}
		return this.registeredImports.add(importMetadata);
	}
	
	/**
	 * Adds the given imports to this builder, if not already present
	 * 
	 * @param imports the imports to add; can be <code>null</code> for none
	 * @return <code>true</code> if the state of this builder changed
	 */
	public boolean add(final Collection<ImportMetadata> imports) {
		if (imports == null) {
			return false;
		}
		return this.registeredImports.addAll(imports);
	}
	
	/**
	 * Copies this builder's modifications into the given ITD builder
	 * 
	 * @param targetBuilder the ITD builder to receive the additions (required)
	 */
	public void copyTo(final AbstractMemberHoldingTypeDetailsBuilder<?> targetBuilder) {
		
		// Copy fields
		for (FieldMetadataBuilder field : getDeclaredFields()) {
			boolean fieldExists = false;
			for (FieldMetadataBuilder targetField : targetBuilder.getDeclaredFields()) {
				if (targetField.getFieldType().equals(field.getFieldType()) && targetField.getFieldName().equals(field.getFieldName())) {
					fieldExists = true;
				}
			}
			if (!fieldExists) {
				targetBuilder.addField(field.build());
			}
		}
		
		// Copy methods
		for (MethodMetadataBuilder method : getDeclaredMethods()) {
			targetBuilder.addMethod(method);
		}
		
		// Copy annotations
		for (AnnotationMetadataBuilder annotation : getAnnotations()) {
			targetBuilder.addAnnotation(annotation);
		}
		
		// Copy custom data
		if (getCustomData() != null) {
			targetBuilder.append(getCustomData().build());
		}
		
		// Copy constructors
		for (ConstructorMetadataBuilder constructor : getDeclaredConstructors()) {
			targetBuilder.addConstructor(constructor);
		}
		
		// Copy initializers
		for (InitializerMetadataBuilder initializer : getDeclaredInitializers()) {
			targetBuilder.addInitializer(initializer);
		}
		
		// Copy inner types
		for (ClassOrInterfaceTypeDetailsBuilder innerType : getDeclaredInnerTypes()) {
			targetBuilder.addInnerType(innerType);
		}
		
		// Copy extends types
		for (JavaType type : getExtendsTypes()) {
			targetBuilder.addExtendsTypes(type);
		}
		
		// Copy implements types
		for (JavaType type : getImplementsTypes()) {
			targetBuilder.addImplementsType(type);
		}
	}
}
