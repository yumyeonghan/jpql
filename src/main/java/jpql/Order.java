package jpql;

import org.hibernate.procedure.ProcedureOutputs;

import javax.persistence.*;

@Entity
@Table(name="ORDERS")//ORDER 는 데이터베이스에서 예약어? 이기때문에 이름을 바꿔야 생성됨.
public class Order {
    @Id @GeneratedValue
    private Long id;
    private int orderAmount;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)@JoinColumn(name="PRODUCT_ID")
    Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
