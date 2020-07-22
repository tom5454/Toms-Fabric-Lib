package com.tom.fabriclibs.mixinapi;

import java.util.Collection;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.InjectionPoint.AtCode;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@AtCode("INVOKE_AFTER")
public class AfterInvoke2 extends BeforeInvoke {

	public AfterInvoke2(InjectionPointData data) {
		super(data);
	}

	@Override
	protected boolean addInsn(InsnList insns, Collection<AbstractInsnNode> nodes, AbstractInsnNode insn) {
		nodes.add(InjectionPoint.nextNode(insns, insn));
		return true;
	}
}
