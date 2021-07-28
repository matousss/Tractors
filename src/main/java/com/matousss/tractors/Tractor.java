package com.matousss.tractors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Tractor implements Listener {
//    public static final double LENGTH = 2.0;
//    public static final double WIDTH = 2.0;
//    public static final double FRONT = 2.0;
//    public static final double BACK = FRONT - LENGTH;
//    public static final double SIDE = WIDTH / 2.0;


    public ItemStack TRACTOR_ITEM;

    public final float STEERING;
    public final float SPEED;
    public final float REVERSE_SPEED;


    public Tractor(float steering, float speed, float reverseSpeed, Material material, String name) {
        super();
        this.TRACTOR_ITEM = initTractor(material, name);
        addMovement();
        STEERING = steering;
        SPEED = speed;
        REVERSE_SPEED = reverseSpeed;


    }

    public Tractor() {
        this(3f, .08f, .06f, Material.STRUCTURE_BLOCK, "§6Traktor");
    }
    public Tractor(Material material, String name) {
        this(3f, .08f, .06f, material, name);
    }


    protected ItemStack initTractor(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    protected void addMovement() {
        Tractors.getInstance().getProtocolManager().addPacketListener(
                new PacketAdapter(Tractors.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE &&
                                event.getPlayer().getVehicle() instanceof ArmorStand) {
                            ArmorStand armorStand = (ArmorStand) event.getPlayer().getVehicle();
                            PacketContainer packet = event.getPacket();

                            float forward = packet.getFloat().readSafely(1);
                            float steering = packet.getFloat().readSafely(0);


                            if (forward != 0) {
                                Location location = armorStand.getLocation();
                                location.add(location.getDirection().normalize().setY(0));
                                double y = armorStand.getVelocity().getY();
//                                if (y <= -.1) {
////                                    Location down = location.clone();
////                                    down.setY(down.getY() - 1);
////                                    if (down.getBlock().getType().isSolid()) {
////                                        y = 0;
////                                    }
//                                }

                                if (location.clone().getBlock().getType().isSolid()) {
                                    armorStand.setVelocity(new Vector().setX(0).setZ(0).setY(y));
                                    return;
                                }

                                Vector velocity = armorStand.getLocation().getDirection().normalize();


                                float rotation = 0;
                                if (steering != 0) {

                                    if (forward < 0) {
                                        if (steering > 0) rotation = STEERING;
                                        else rotation = -STEERING;

                                    } else {
                                        if (steering > 0) rotation = -STEERING;
                                        else rotation = STEERING;
                                    }
                                }

                                velocity.multiply(forward > 0 ? SPEED : -REVERSE_SPEED)
                                        .setY(armorStand.getVelocity().getY());


                                if (rotation != 0) armorStand.setRotation(
                                        armorStand.getLocation().getYaw() + rotation,
                                        armorStand.getLocation().getPitch());
                                armorStand.setVelocity(velocity);
                            }


                        }
                    }
                });

    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.hasItem() && event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!event.getItem().isSimilar(TRACTOR_ITEM)) return;
        event.setCancelled(true);

        if (event.getBlockFace() != BlockFace.UP) return;

        ItemStack itemStack = new ItemStack(event.getItem());
        itemStack.setAmount(1);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.getItem().setAmount(event.getItem().getAmount() - 1);

        Location location = new Location(event.getClickedBlock().getWorld(), 0, 0, 0)
                .add(event.getClickedBlock().getLocation());


        location.setY(location.getY() + 1);
        location.setDirection(event.getPlayer().getLocation().getDirection());
        location.add(.5, 0 , .5);

        ArmorStand e = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        e.setGravity(true);
        e.setInvisible(true);
        //e.setInvulnerable(true);
        e.setBasePlate(false);
        e.setHeadPose(new EulerAngle(Math.PI, 0, 0));
        e.setCollidable(false);
        e.setSmall(true);
        e.getEquipment().setHelmet(itemStack);

        e.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        e.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING);
        e.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING);
        e.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING);
    }


    public boolean isTractor(Entity entity) {
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) entity;
            try {
                return armorStand.getEquipment().getHelmet().isSimilar(TRACTOR_ITEM);
            } catch (NullPointerException e) {
                return false;
            }

        }
        return false;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (isTractor(event.getRightClicked())) {
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();
            if (armorStand.getPassengers().size() == 0) {
                armorStand.addPassenger(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    private void destroyTractor(ArmorStand armorStand) {
        Item item = (Item) armorStand.getLocation().getWorld().spawnEntity(
                armorStand.getLocation().add(0, 1, 0), EntityType.DROPPED_ITEM);
        item.setItemStack(Objects.requireNonNull(Objects.requireNonNull(armorStand.getEquipment()).getHelmet()));
        armorStand.getEquipment().setHelmet(null);
        armorStand.remove();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isTractor(event.getEntity())) {
            if (event.getDamager() instanceof Player) {
                ArmorStand armorStand = (ArmorStand) event.getEntity();
                destroyTractor(armorStand);
                armorStand.remove();
            } else event.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (isTractor(event.getEntity())) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            destroyTractor(armorStand);
        }
    }


}

