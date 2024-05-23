package fr.maxlego08.essentials.buttons;

import fr.maxlego08.essentials.api.EssentialsPlugin;
import fr.maxlego08.essentials.api.mailbox.MailBoxItem;
import fr.maxlego08.essentials.api.user.User;
import fr.maxlego08.essentials.module.modules.MailBoxModule;
import fr.maxlego08.essentials.zutils.utils.ComponentMessageHelper;
import fr.maxlego08.essentials.zutils.utils.TimerBuilder;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.inventory.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonMailBox extends ZButton implements PaginateButton {

    private final EssentialsPlugin plugin;

    public ButtonMailBox(Plugin plugin) {
        this.plugin = (EssentialsPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {

        User user = plugin.getUser(player.getUniqueId());
        if (user == null) return;

        List<MailBoxItem> mailBoxItems = user.getMailBoxItems().stream().filter(item -> !item.isExpired()).toList();
        Pagination<MailBoxItem> pagination = new Pagination<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        pagination.paginate(mailBoxItems, this.slots.size(), inventory.getPage()).forEach(mailBoxItem -> displayItem(this.slots.get(atomicInteger.getAndIncrement()), mailBoxItem, player, user, inventory));
    }

    private void displayItem(int slot, MailBoxItem mailBoxItem, Player player, User user, InventoryDefault inventory) {

        MailBoxModule mailBoxModule = this.plugin.getModuleManager().getModule(MailBoxModule.class);

        Placeholders placeholders = new Placeholders();
        placeholders.register("expiration", TimerBuilder.getStringTime(mailBoxItem.getExpiredAt().getTime() - System.currentTimeMillis()));

        ItemStack itemStack = mailBoxItem.getItemStack().clone();
        ComponentMessageHelper.componentMessage.addToLore(itemStack, this.getItemStack().getLore(), placeholders);

        inventory.addItem(slot, itemStack).setClick(event -> mailBoxModule.removeItem(user, player, mailBoxItem));
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        User user = plugin.getUser(player.getUniqueId());
        return user != null && user.getMailBoxItems().size() > 0;
    }

    @Override
    public int getPaginationSize(Player player) {
        User user = plugin.getUser(player.getUniqueId());
        return user == null ? 0 : user.getMailBoxItems().size();
    }
}