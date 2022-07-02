import jpql.Member;
import jpql.MemberDTO;
import jpql.MemberType;
import jpql.Team;

import javax.persistence.*;
import java.util.List;

public class jpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team=new Team();
            team.setName("teamA");
            em.persist(team);

            Member member =new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.changeTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            //from 절에서 명시적 조인을 하면 별칭을 얻을수 있다. 이를 이용해 컬렉션 값 연관 경로에서 탐색이 가능하게 한다.
            String query="select m.username From Team t join t.members m";
            TypedQuery<Member> query1 = em.createQuery(query, Member.class);

            /***
            //ENUM 을 표현하려면 패키지명부터 다 써야함. 하지만 파라미터 바인딩을 하면 짧게 표현 가능.
            String query= "select m.username, 'Hello', true From Member m "+
                          "where m.type=:userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType",MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }



            //내부 조인, inner 대신 외부조인 (left outer join) 사용 가능
            //조인된 테이블이기 때문에 Select m 대신에 t도 사용 가능
            List<Member> resultList1 = em.createQuery("select m from Member m inner join m.team t", Member.class)
                    .getResultList();

            //세타 조인(막 조인)
            List<Member> resultList2 = em.createQuery("select m from Member m, Team t where m.username=t.name", Member.class)
                    .getResultList();

            //페이징 예시
            String jpql= "select m from Member m order by m.username desc";
            List<Member> resultList = em.createQuery(jpql, Member.class)
                    .setFirstResult(10)//10번째 데이터부터
                    .setMaxResults(20)//20개를 가져온다.
                    .getResultList();


            //new 명령어로 조회(단점은 패키지가 길어지면 아래처럼 다 적어야하는 한계가 있음)
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());


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

