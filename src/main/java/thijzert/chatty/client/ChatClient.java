package thijzert.chatty.client;

import thijzert.chatty.data.UserInfo;

/**
 * Interface of a chatclient.
 *
 * @author Thijzert
 */
public interface ChatClient {
    /**
     * Returns the <code>UserInfo</code>.
     *
     * @return the <code>UserInfo</code>
     * @see UserInfo
     */
    UserInfo getUserInfo();
}
