package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import scala.tools.nsc.transform.patmat.Logic;

import java.util.Arrays;
import java.util.TreeSet;

public class DropMiningItems {

    private static final TreeSet<String> filterList = new TreeSet<>();

    static {
        String[] whiteList = {
                "minecraft:lapis_ore",
                "minecraft:quartz_ore",
                "minecraft:emerald_ore",
                "minecraft:stained_hardened_clay",
                "minecraft:mycelium",
                "minecraft:prismarine",
                "minecraft:red_sandstone",
                "minecraft:sea_lantern",
                "minecraft:grass",
                "minecraft:stone",
                "minecraft:dirt",
                "minecraft:cobblestone",
                "minecraft:sand",
                "minecraft:gravel",
                "minecraft:gold_ore",
                "minecraft:iron_ore",
                "minecraft:coal_ore",
                "minecraft:log",
                "minecraft:sandstone",
                "minecraft:soul_sand",
                "minecraft:netherrack",
                "minecraft:clay",
                "minecraft:magma",
                "minecraft:end_stone",
                "minecraft:stonebrick",
                "minecraft:glowstone",
                "minecraft:redstone_ore",
                "minecraft:mossy_cobblestone",
                "minecraft:nether_brick_fence",
                "minecraft:purpur_block",
                "minecraft:purpur_stairs",
                "minecraft:end_bricks",
                "minecraft:diamond_ore",
                "minecraft:obsidian",
                "minecraft:nether_brick",
                "minecraft:nether_brick_stairs",
        };
        filterList.addAll(Arrays.asList(whiteList));
    }

    public static void dropMiningItems() {
        boolean dropAll = CutelessModUtils.isCtrlKeyDown();
        InventoryPlayer inventory = Minecraft.getMinecraft().player.inventory;
        NonNullList<ItemStack> invList = inventory.mainInventory;
        for (int slotNumber = 0; slotNumber < invList.size(); slotNumber++) {
            ItemStack itemStack = invList.get(slotNumber);

            ResourceLocation registryName = itemStack.getItem().getRegistryName();
            if (registryName != null && filter(registryName.toString())) {
                if (slotNumber < 9) {
                    setHotBarSlot(slotNumber);
                    int count = itemStack.getCount();
                    dropHotBar(dropAll, inventory, slotNumber, count);
                } else {
                    if (dropAll) {
                        dropAll(slotNumber);
                    } else {
                        dropAllButOne(slotNumber);
                    }
                }
            } else {
                System.out.println(registryName.toString());
            }
        }
        setHotBarSlot(inventory.currentItem);
    }

    private static boolean filter(String filterString) {
        return filterList.contains(filterString);
    }

    private static void setHotBarSlot(int slotNumber) {
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketHeldItemChange(slotNumber));
    }

    private static void dropHotBar(boolean dropAll, InventoryPlayer inv, int slotNumber, int itemCount) {
        CPacketPlayerDigging.Action packetType;
        if (dropAll) {
            packetType = CPacketPlayerDigging.Action.DROP_ALL_ITEMS;
            inv.decrStackSize(slotNumber, itemCount);
            itemCount = 1;
        } else {
            packetType = CPacketPlayerDigging.Action.DROP_ITEM;
            itemCount -= 1;
            inv.decrStackSize(slotNumber, itemCount);
        }
        for (int i = 0; i < itemCount; i++) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(packetType, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }

    private static void dropAll(int slotNumber) {
        clickSlot(slotNumber, 0, ClickType.PICKUP);
        clickSlot(-999, 0, ClickType.PICKUP);
    }

    private static void dropAllButOne(int slotNumber) {
        clickSlot(slotNumber, 0, ClickType.PICKUP);
        clickSlot(slotNumber, 1, ClickType.PICKUP);
        clickSlot(-999, 0, ClickType.PICKUP);
    }

    public static void clickSlot(int slotNumber, int mouseButton, ClickType type) {
        try {
            handleMouseClick(slotNumber, mouseButton, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleMouseClick(int slotNum, int mouseButton, ClickType type) {
        int windowId = Minecraft.getMinecraft().player.inventoryContainer.windowId;
        Minecraft.getMinecraft().playerController.windowClick(windowId, slotNum, mouseButton, type, Minecraft.getMinecraft().player);
    }
}