package me.anky.connectid.connections;

import java.util.List;

import me.anky.connectid.data.ConnectidConnection;

public interface ConnectionsActivityMVP {

    interface View {

        void displayConnections(List<ConnectidConnection> connections);

        void displayNoConnections();

//        Single<Integer> getSortByOption();
        int getSortByOption();

        void displayError();
    }

    interface Presenter {

        void setView(ConnectionsActivityMVP.View view);

        void loadConnections(Integer integer);

        void handleSortByOptionChange();

        void unsubscribe();
    }
}
