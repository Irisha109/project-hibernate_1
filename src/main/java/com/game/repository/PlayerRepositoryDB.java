package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import jakarta.persistence.*;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
       // properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
       // properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");

        properties.put(Environment.HBM2DDL_AUTO, "update");
       // properties.put(Environment.SHOW_SQL, "true");

        sessionFactory = new Configuration()
                .addAnnotatedClass(com.game.entity.Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {

        String sql = "SELECT * FROM player";
        try(Session session = sessionFactory.openSession()){
        NativeQuery<Player> query = session.createNativeQuery(sql, Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);

        return query.getResultList();
        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
            Query<Long> query = session.createNamedQuery("Player_FindAllCountPlayers", Long.class);
            return  query.uniqueResult().intValue();
        }

    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
            return player;

        }

    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;

        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try(Session session = sessionFactory.openSession()){
            Player  playerId= (Player)  session.get(Player.class, id);

           return Optional.ofNullable(playerId);

        }

    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()) {
            Player player1 = session.get(Player.class, player.getId());
            Transaction transaction = session.beginTransaction();
            session.remove(player1);
            transaction.commit();

        }

    }

    @PreDestroy
    public void beforeStop() {
    sessionFactory.close();
    }




}