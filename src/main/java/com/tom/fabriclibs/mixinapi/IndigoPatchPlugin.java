package com.tom.fabriclibs.mixinapi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IndigoPatchPlugin implements IMixinConfigPlugin {

	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface To {}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		System.out.println("Applying mixin: " + targetClassName);
		ClassNode mixin = mixinInfo.getClassNode(0);
		MethodNode method = null;
		AnnotationNode an = null;
		for(MethodNode m : mixin.methods) {
			if(m.visibleAnnotations != null) {
				for(AnnotationNode a : m.visibleAnnotations) {
					if(a.desc.equals("Lcom/tom/fabriclibs/mixinapi/IndigoPatchPlugin$To;")) {
						method = m;
						an = a;
						break;
					}
				}
				if(method != null)break;
			}
		}
		MethodNode into = null;
		for(MethodNode m : targetClass.methods) {
			if(m.name.equals("accept")) {
				into = m;
				break;
			}
		}
		into.instructions.clear();
		into.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		into.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		into.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, targetClassName.replace('.', '/'), method.name, method.desc));
		into.instructions.add(new InsnNode(Opcodes.RETURN));
		System.out.println("Applied redirect");
	}

}
