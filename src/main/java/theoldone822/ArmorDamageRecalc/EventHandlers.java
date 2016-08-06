package theoldone822.ArmorDamageRecalc;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlers {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void HurtEvent(LivingHurtEvent event) {
		if (!event.getSource().isUnblockable()/* && event.getEntityLiving() instanceof EntityPlayer*/) {

		float armF = (float) event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
		if ((!ArmorDamageRecalc.trueVanilla && armF > 0) || (ArmorDamageRecalc.trueVanilla && event.getEntityLiving().getTotalArmorValue() > 0)) {
			float baseDamage = event.getAmount();
			if (ArmorDamageRecalc.trueVanilla) {
				event.setAmount(CombatRules.getDamageAfterAbsorb(event.getAmount(), (float) event.getEntityLiving().getTotalArmorValue(), (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
				if (event.getEntityLiving() instanceof EntityPlayer) {
					//vanilla way to damage armor
					((EntityPlayer)event.getEntityLiving()).inventory.damageArmor(baseDamage);
					
				}
			} else {
				event.setAmount(CombatRules.getDamageAfterAbsorb(event.getAmount(), armF, (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
				if (event.getEntityLiving() instanceof EntityPlayer) {
					for (int x = 0; x < ((EntityPlayer)event.getEntityLiving()).inventory.armorInventory.length; x++){
				          ItemStack stack = ((EntityPlayer)event.getEntityLiving()).inventory.armorInventory[x];
				          if (stack == null)
				          {
				              continue;
				          }
				          int itemDamage = (int) (((event.getAmount() - baseDamage) * -1 * (((ItemArmor)stack.getItem()).damageReduceAmount) / armF) < 1 ? 1 : ((event.getAmount() - baseDamage) * -1 * (((ItemArmor)stack.getItem()).damageReduceAmount) / armF));
				          if (stack.getItem() instanceof ISpecialArmor)
				          {
				        	  ((ISpecialArmor)stack.getItem()).damageArmor(event.getEntityLiving(), stack, event.getSource(), itemDamage, x);
	                      }
				          else
				          {
				        	  stack.damageItem(itemDamage, event.getEntityLiving());
				          }
					}
				}
			}

			//Stuff for broken AT
/*		//if it's a player we also need to rebuild the potion damage and check absorption to get the hunger damage right
		if (event.getEntityLiving() instanceof EntityPlayer) {
			if (!event.getSource().isDamageAbsolute()) {
	            if (event.getEntityLiving().isPotionActive(MobEffects.RESISTANCE) && event.getSource() != DamageSource.outOfWorld)
	            {
	                int i = (event.getEntityLiving().getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
	                int j = 25 - i;
	                float f = event.getAmount() * (float)j;
	                event.setAmount(f / 25.0F);
	            }

	            if (event.getAmount() <= 0.0F)
	            {
	                return;
	            }
	            else
	            {
	                int k = EnchantmentHelper.getEnchantmentModifierDamage(event.getEntityLiving().getArmorInventoryList(), event.getSource());

	                if (k > 0)
	                {
	                	event.setAmount(CombatRules.getDamageAfterMagicAbsorb(event.getAmount(), (float)k));
	                }
	            }
	            if (event.getAmount() > event.getEntityLiving().getAbsorptionAmount());{
	            	((EntityPlayer)event.getEntityLiving()).addExhaustion(event.getSource().getHungerDamage());
	            }
	            event.getSource().setDamageIsAbsolute();
			} else {
				
	            if (event.getAmount() > event.getEntityLiving().getAbsorptionAmount());{
	            	((EntityPlayer)event.getEntityLiving()).addExhaustion(event.getSource().getHungerDamage());
	            }
			}
		}
		*/
		
		float h = event.getSource().getHungerDamage();
		event.getSource().setDamageBypassesArmor();
		event.getSource().hungerDamage = h;
		
		
		
		}
	}
		
		
// Stuff from 1.9 version that may be needed if forge ever fixes there armor system		
/*		if (!event.getSource().isUnblockable() && event.getEntityLiving() instanceof EntityPlayer) {

			float armF = (float) event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
			if (armF > 0) {
				if (armF < 1) {
					event.setAmount(CombatRules.getDamageAfterAbsorb(event.getAmount(), armF, (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
				} else {
					if (armF != (float) event.getEntityLiving().getTotalArmorValue()) {
						float vaDam = CombatRules.getDamageAfterAbsorb(event.getAmount(), (float) event.getEntityLiving().getTotalArmorValue(), (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
						float waDam = CombatRules.getDamageAfterAbsorb(event.getAmount(), armF, (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

						if ((float) event.getEntityLiving().getTotalArmorValue() - event.getAmount() * 0.5F < (float) event.getEntityLiving().getTotalArmorValue() * 0.2F) {
							event.setAmount((1 - (float) event.getEntityLiving().getTotalArmorValue() * 0.2F) * waDam);
						} else if ((float) event.getEntityLiving().getTotalArmorValue() - event.getAmount() * 0.5f > 20.0F) {
							event.setAmount((1 - 20.0F / 25) * waDam);
						} else {
							event.setAmount((1 - ((float) event.getEntityLiving().getTotalArmorValue() - event.getAmount() * 0.5F) / 25) * waDam);
						}
					}
				}
			}
		}*/
	}
}
