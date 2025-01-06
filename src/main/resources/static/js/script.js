/**
 * 
 */

 console.log("Smart Contact Manager Application");

 const toggleSidebar= ()=>{

    if($(".sidebar").is(":visible")){
        //true
        //sidebar band karna hai
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");


    }else{
        //false
        //sidebar show karna hai
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");


    }

 };

 const search= ()=>{
    //console.log("Searching......");

    let query= $("#search-input").val();
    

    if(query==''){

        $(".search-result").hide();

    }else{
        //search
         console.log(query);



         //sending request to server

         let url= `http://localhost:8080/search/${query}`;


         fetch(url).then(response=>{
            return response.json();
         }).then((data)=>{
            //data

            console.log(data);

            let text= `<div class='list-group'>`;

            data.forEach(contact => {
                
                text+= `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'>${contact.name}</a>`;

            });


            text+=`</div>`;

            $(".search-result").html(text);

            $(".search-result").show();

         });
    
    }

 };
 
 
 //first request to server to create order
 
 const paymentStarted=()=>{
	
	console.log("Payment Started....");
	
	let amount= $("#payment_field").val();
	
	console.log(amount);
	
	if(amount==''|| amount==null){
		
		//alert("Amount is required!!");
		
		Swal.fire({
										  title: "Uhhh...",
										  text: "Amount is required!!",
										  icon: "error"
										});
		
		return;
	}
	
	//code....
	//we will use ajax to send request to server to create order- jquery
	
	$.ajax(
		
		{
			url:'/user/create-order',
			data: JSON.stringify({amount:amount, info: 'order_request'}),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(response){
				//invoked when success
				console.log(response);
				
				if(response.status=="created"){
					
					//open payment form
					
					let options={
						
						"key": "rzp_test_2ksHbAkbx28n0p",
						"amount": response.amount,
						"currency": "INR",
						"name": "Smart Contact Manager",
						"description": "Doantion",
						"image":"https://lh3.googleusercontent.com/a/ACg8ocKWgT7d0h-itp-8nk1C9NHl4RA1G8fCYUfp-65ZJ9Z-ovmRMx38=s288-c-no",
						"orderId": response.id,
						"handler": function(response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log("Payment Successful...");
							
							//alert("Congrats! Payment Successful..");
							
							Swal.fire({
										  title: "Good job!",
										  text: "Congrats! Payment Successful..",
										  icon: "success"
										});

						},
						"prefill": {
									"name": "Akash Singh",
									"email": "akash214singh@gmail.com",
									"contact": "9013666805"
									},
									"notes": {
											"address": "Akash Technologies"},
											"theme": {
													"color": "#3399cc"
													}								
					};

					var rzp = new Razorpay(options);
						rzp.on('payment.failed', function (response){
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						//alert("OOPS Payment failed!!!")
						
						Swal.fire({
										  title: "Failed!",
										  text: "OOPS Payment failed!!!",
										  icon: "error"
										});
						
						});
						
						rzp.open();
					
				}
				
			},
			
			error: function(error){
				//invoked when error
				console.log(error);
				console.log("Something went wrong!!");
			}
			
		}
	)
	
 };