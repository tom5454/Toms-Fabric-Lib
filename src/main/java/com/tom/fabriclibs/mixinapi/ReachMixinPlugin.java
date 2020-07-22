package com.tom.fabriclibs.mixinapi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class ReachMixinPlugin implements IMixinConfigPlugin {

	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface MixinInject {
		double value();
		String obf();
		String normal();
	}

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
					if(a.desc.equals("Lcom/tom/fabriclibs/mixinapi/ReachMixinPlugin$MixinInject;")) {
						method = m;
						an = a;
						break;
					}
				}
				if(method != null)break;
			}
		}
		Map<String, Object> anMap = new HashMap<>();
		for (int i = 0; i < an.values.size(); i+=2) {
			anMap.put((String) an.values.get(i), an.values.get(i+1));
		}
		System.out.println(anMap);
		MethodNode into = null;
		for(MethodNode m : targetClass.methods) {
			if(m.name.equals(anMap.get("obf")) || m.name.equals(anMap.get("normal"))) {
				into = m;
				break;
			}
		}
		for(AbstractInsnNode node : into.instructions) {
			if(node instanceof LdcInsnNode) {
				LdcInsnNode ldc = (LdcInsnNode) node;
				//System.out.println("LDC: " + ldc.cst);
				if(ldc.cst.equals(anMap.get("value"))) {
					into.instructions.insertBefore(ldc, new VarInsnNode(Opcodes.ALOAD, 0));
					into.instructions.insert(ldc, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, targetClass.name, method.name, method.desc));
					break;
					//System.out.println("Found");
				}
			}
		}
	}

}
