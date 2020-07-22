package com.tom.fabriclibs.ext;

import net.minecraft.util.Identifier;

public interface IRegistered {
	default Identifier getRegistryName()  {throw new AbstractMethodError("Mixin failed");}
}
