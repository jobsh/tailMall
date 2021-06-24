package online.loopcode.tailmall.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.loopcode.tailmall.model.dto.SkuInfoDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderSku {
    private Long id;
    private Long spuId;
    /** sku单价 * 当前sku数量 */
    private BigDecimal finalPrice;
    /** sku单价 */
    private BigDecimal singlePrice;
    /** 当前sku规格值 */
    private List<String> specValues;
    /** 购买的此sku的数量 */
    private Integer count;
    private String img;
    private String title;

    public OrderSku(Sku sku, SkuInfoDTO skuInfoDTO) {

        this.id = sku.getId();
        this.spuId = sku.getSpuId();
        this.singlePrice = sku.getActualPrice();
        this.finalPrice = sku.getActualPrice().multiply(new BigDecimal(skuInfoDTO.getCount()));
        this.count = skuInfoDTO.getCount();
        this.img = sku.getImg();
        this.title = sku.getTitle();
        this.specValues = sku.getSpecValueList();
    }
}
