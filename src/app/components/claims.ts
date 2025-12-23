import { User } from "./users";

export interface Claim{
    claimid : number,
    userid : number,
    descriptioncode : string,
    procedurecode : string,
    price : number,
    datesubmitted : string,
}