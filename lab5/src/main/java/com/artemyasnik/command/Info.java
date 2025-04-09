package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Info extends Command {
    Info() {
        super("info", "displays information about the collection");
    }


    @Override
    public Response execute(Request request) {
        return new Response(CollectionManager.getInstance().info());
    }
}
