document.addEventListener('alpine:init', () => {
    Alpine.data('initData', (pageNo) => ({
        pageNo: pageNo,
        products: {
            data: []
        },
        init() {
            this.loadProducts(this.pageNo);
            updateCartItemCount();
        },
        loadProducts(pageNo) {
           // $.getJSON("http://localhost:8989/catalog/api/products?page="+pageNo, (resp)=> { //before api gateway env creation in layout.html
            //$.getJSON(apiGatewayUrl+"/catalog/api/products?page="+pageNo, (resp)=> { // after api gateway env creation in layout.html
            $.getJSON("/api/products?page="+pageNo, (resp)=> {//using only /api/products as per new api gateway mapping using CatalogServiceClient interface
                console.log("Products Resp:", resp)
                this.products = resp;
            });
        },
        addToCart(product) {
            addProductToCart(product)
        }
    }))
});