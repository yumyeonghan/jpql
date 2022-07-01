import jpql.Member;
import jpql.MemberDTO;

import javax.persistence.*;
import java.util.List;

public class jpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member =new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            //new 명령어로 조회(단점은 패키지가 길어지면 아래처럼 다 적어야하는 한계가 있음)
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());

            /***
            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);//Member.class 는 반환할 타입임
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");//반환 타입이 명확하지 않을때 Query 사용.

            List<Member> resultList = query1.getResultList();//받아올수 있는 값이 여러개일때 리스트로 받고, 없어도 빈 리스트 반환

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }//리스트로 받은 결과 출력

            //파라미터 바인딩-이름 기준(위치 기준으론 사용하지 말자)
            //보통은 이렇게 말고 체이닝으로( .)으로 다엮어서 표현
            query1 = em.createQuery("select m from Member m where m.username=:username", Member.class);
            query1.setParameter("username", "member1");
            query1.getResultList();

            System.out.println("resultList = " + resultList.get(0).getUsername());
             ***/



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}

