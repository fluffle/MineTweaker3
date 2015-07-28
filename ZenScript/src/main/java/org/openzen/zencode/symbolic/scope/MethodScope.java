/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.symbolic.scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.openzen.zencode.ICodeErrorLogger;
import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.IZenCompileEnvironment;
import org.openzen.zencode.ZenPackage;
import org.openzen.zencode.compiler.IExpressionCompiler;
import org.openzen.zencode.compiler.TypeRegistry;
import org.openzen.zencode.symbolic.AccessScope;
import org.openzen.zencode.symbolic.type.generic.GenericParameter;
import org.openzen.zencode.symbolic.method.MethodHeader;
import org.openzen.zencode.symbolic.statement.Statement;
import org.openzen.zencode.symbolic.symbols.IZenSymbol;
import org.openzen.zencode.symbolic.type.generic.TypeCapture;
import org.openzen.zencode.symbolic.definition.ISymbolicDefinition;
import org.openzen.zencode.symbolic.type.IGenericType;
import org.openzen.zencode.symbolic.type.ParameterType;
import org.openzen.zencode.symbolic.type.TypeInstance;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stanneke
 * @param <E>
 */
public class MethodScope<E extends IPartialExpression<E>> implements IMethodScope<E>
{
	private final IDefinitionScope<E> scope;
	private final Map<String, IZenSymbol<E>> local;
	private final MethodHeader<E> methodHeader;
	private final TypeCapture<E> typeCapture;
	private final boolean isConstructor;
	
	public MethodScope(IDefinitionScope<E> scope, MethodHeader<E> methodHeader, boolean isConstructor)
	{
		this.scope = scope;
		this.local = new HashMap<>();
		this.methodHeader = methodHeader;
		this.isConstructor = isConstructor;
		
		typeCapture = new TypeCapture<>(scope.getTypeCapture());
		for (GenericParameter<E> parameter : methodHeader.getGenericParameters()) {
			typeCapture.put(parameter, new TypeInstance<>(new ParameterType<E>(this, parameter), Collections.emptyList(), false));
		}
	}
	
	@Override
	public ISymbolicDefinition<E> getDefinition()
	{
		return scope.getDefinition();
	}

	@Override
	public AccessScope getAccessScope()
	{
		return scope.getAccessScope();
	}

	@Override
	public TypeRegistry<E> getTypeCompiler()
	{
		return scope.getTypeCompiler();
	}
	
	@Override
	public IMethodScope<E> getConstantScope()
	{
		return scope.getConstantScope();
	}

	@Override
	public IZenCompileEnvironment<E> getEnvironment()
	{
		return scope.getEnvironment();
	}
	
	@Override
	public IExpressionCompiler<E> getExpressionCompiler()
	{
		return scope.getExpressionCompiler();
	}

	@Override
	public IPartialExpression<E> getValue(String name, CodePosition position, IMethodScope<E> environment)
	{
		if (local.containsKey(name))
			return local.get(name).instance(position, environment);
		else
			return scope.getValue(name, position, environment);
	}

	@Override
	public void putValue(String name, IZenSymbol<E> value, CodePosition position)
	{
		if (local.containsKey(name))
			getErrorLogger().errorSymbolNameAlreadyExists(position, name);
		else
			local.put(name, value);
	}

	@Override
	public Statement<E> getControlStatement(String label)
	{
		return null;
	}

	@Override
	public IGenericType<E> getReturnType()
	{
		return methodHeader.getReturnType();
	}

	@Override
	public ICodeErrorLogger<E> getErrorLogger()
	{
		return scope.getErrorLogger();
	}

	@Override
	public MethodHeader<E> getMethodHeader()
	{
		return methodHeader;
	}

	@Override
	public TypeCapture<E> getTypeCapture()
	{
		return typeCapture;
	}

	@Override
	public ZenPackage<E> getRootPackage()
	{
		return scope.getRootPackage();
	}

	@Override
	public IZenSymbol<E> getSymbol(String name)
	{
		if (local.containsKey(name))
			return local.get(name);
		
		return scope.getSymbol(name);
	}

	@Override
	public boolean contains(String name)
	{
		return local.containsKey(name) || scope.contains(name);
	}

	@Override
	public void putImport(String name, IZenSymbol<E> symbol, CodePosition position)
	{
		putValue(name, symbol, position);
	}
	
	@Override
	public boolean isConstructor()
	{
		return isConstructor;
	}

	@Override
	public IGenericType<E> getSelfType()
	{
		return scope.getSelfType();
	}

	@Override
	public E getThis(CodePosition position, IGenericType<E> predictedType)
	{
		return getExpressionCompiler().thisValue(position, this);
	}
}