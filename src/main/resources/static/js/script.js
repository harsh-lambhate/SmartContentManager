console.log("this is script file");

const toggleSidebar =() =>{
    if($('.sidebar').is(":visible")){
       //true
       //band karna hai

       $(".sidebar").css("display","none");
       $(".content").css("margin-left","0%");
    }
    else
    {
        //false
        //show karna hai 
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }

};

const search = () => {

/* console.log("searching..");*/
   
   let query=$("#search-input").val();
   
   //console.log(query);
   
   if(query == "")
   {
      $(".search-result").hide();
   }
   else
     {
       console.log(query);
       
       //sending request to server
       let url = `http://localhost:8282/search/${query}`;
       
       fetch(url)
       .then((response) =>{
         
         return response.json();
       })
       .then((data) =>{
           //data....
           //console.log(data);
          
            let text =`<div class='list-group'>`
            
            data.forEach((contact) => {
            
            text += `<a href='#' class='list-group-item list-group-item-action'> ${contact.name} </a>`
            
            $(".search-result").html(text);
            $(".search-result").show();
            });
            
            text +=`</div>`;
           
       });
       
       $(".search-result").show();
       
     } 
};